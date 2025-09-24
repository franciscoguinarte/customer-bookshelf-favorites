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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteBookService {

    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final RestTemplate restTemplate;

    @Value("${brasilapi.url}")
    private String brasilApiUrl;

    @Transactional(readOnly = true)
    public Set<BookDTO> getFavoriteBooks(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return customer.getFavoriteBooks().stream()
                .map(BookMapper::toDTO)
                .collect(Collectors.toSet());
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

    private BookResponseDTO fetchBookFromBrasilApi(String isbn) {
        try {
            String url = brasilApiUrl + isbn;
            return restTemplate.getForObject(url, BookResponseDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ExternalBookNotFoundException("Book with ISBN " + isbn + " not found in external API.");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch book data from external API.", ex);
        }
    }
}
