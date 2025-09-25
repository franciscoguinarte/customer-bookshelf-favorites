package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.BookDTO;
import com.ancora.customerbookshelf.dto.brasilapi.BookResponseDTO;
import com.ancora.customerbookshelf.exception.BookAlreadyInFavoritesException;
import com.ancora.customerbookshelf.exception.ExternalBookNotFoundException;
import com.ancora.customerbookshelf.exception.ResourceNotFoundException;
import com.ancora.customerbookshelf.mapper.BookMapper;
import com.ancora.customerbookshelf.model.Book;
import com.ancora.customerbookshelf.model.Customer;
import com.ancora.customerbookshelf.model.Dimensions;
import com.ancora.customerbookshelf.repository.BookRepository;
import com.ancora.customerbookshelf.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteBookService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteBookService.class);

    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${brasilapi.url}")
    private String brasilApiUrl;

    @Transactional(readOnly = true)
    public Page<BookDTO> getFavoriteBooks(Long customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        Page<Book> bookPage = bookRepository.findFavoritesByCustomerId(customerId, pageable);
        return bookPage.map(BookMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public BookDTO getFavoriteBookByIsbn(Long customerId, String isbn) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return customer.getFavoriteBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .map(BookMapper::toDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Book with ISBN " + isbn + " not found in customer's favorites."));
    }

    @Transactional
    public void addBookToFavorites(Long customerId, String isbn) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        boolean alreadyFavorited = customer.getFavoriteBooks().stream().anyMatch(book -> book.getIsbn().equals(isbn));
        if (alreadyFavorited) {
            throw new BookAlreadyInFavoritesException("Book with ISBN " + isbn + " is already in the customer's favorites.");
        }

        Book book = bookRepository.findById(isbn).orElseGet(() -> fetchAndSaveBook(isbn));

        customer.getFavoriteBooks().add(book);
        customerRepository.save(customer);
    }

    @Transactional
    public void removeBookFromFavorites(Long customerId, String isbn) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Book bookToRemove = customer.getFavoriteBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Book with ISBN " + isbn + " not found in customer's favorites."));

        customer.getFavoriteBooks().remove(bookToRemove);
        customerRepository.save(customer);
    }

    private Book fetchAndSaveBook(String isbn) {
        BookResponseDTO responseDTO = fetchBookFromBrasilApi(isbn);
        if (responseDTO == null) {
            throw new ExternalBookNotFoundException("Failed to fetch book data for ISBN " + isbn + " from external API after multiple retries.");
        }

        Dimensions dimensions = Optional.ofNullable(responseDTO.getDimensions())
                .map(d -> Dimensions.builder().width(d.getWidth()).height(d.getHeight()).unit(d.getUnit()).build())
                .orElse(null);

        Book newBook = Book.builder()
                .isbn(responseDTO.getIsbn())
                .title(responseDTO.getTitle())
                .subtitle(responseDTO.getSubtitle())
                .authors(responseDTO.getAuthors())
                .publisher(responseDTO.getPublisher())
                .synopsis(responseDTO.getSynopsis())
                .dimensions(dimensions)
                .year(responseDTO.getYear())
                .format(responseDTO.getFormat())
                .pageCount(responseDTO.getPageCount())
                .subjects(responseDTO.getSubjects())
                .location(responseDTO.getLocation())
                .retailPrice(responseDTO.getRetailPrice())
                .coverUrl(responseDTO.getCoverUrl())
                .provider(responseDTO.getProvider())
                .build();

        return bookRepository.save(newBook);
    }

    /**
     * Anotação que define este método como "tentável novamente".
     * value = As exceções que, se lançadas, disparam a lógica de retentativa.
     * maxAttempts = O número máximo de tentativas (a primeira + 2 retentativas).
     * backoff = Define um tempo de espera entre as tentativas (neste caso, 2 segundos).
     */
    @Retryable(
            value = {ResourceAccessException.class, HttpServerErrorException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Cacheable("booksByIsbn")
    public BookResponseDTO fetchBookFromBrasilApi(String isbn) {
        try {
            String url = brasilApiUrl + isbn;
            log.info("Fetching book with ISBN {} from URL: {}", isbn, url);
            return restTemplate.getForObject(url, BookResponseDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            // Esta exceção (404) não dispara a retentativa, pois significa que o livro não existe, então não adianta tentar de novo.
            throw new ExternalBookNotFoundException("Book with ISBN " + isbn + " not found in external API.");
        }
    }

    /**
     * Método de recuperação. É chamado pelo Spring quando o método anotado com @Retryable falha em todas as suas tentativas.
     * A assinatura deve ser a mesma do método original, com a exceção que causou a falha como primeiro parâmetro.
     */
    @Recover
    private BookResponseDTO recoverFromApiFailure(Exception ex, String isbn) {
        log.error("All retry attempts failed for ISBN {}. Error: {}", isbn, ex.getMessage());
        // Retornar nulo é uma forma de sinalizar para o método que o chamou (fetchAndSaveBook) que a operação falhou permanentemente.
        return null;
    }
}
