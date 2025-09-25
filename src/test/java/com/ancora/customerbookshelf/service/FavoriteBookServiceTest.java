package com.ancora.customerbookshelf.service;

import com.ancora.customerbookshelf.dto.brasilapi.BookResponseDTO;
import com.ancora.customerbookshelf.exception.BookAlreadyInFavoritesException;
import com.ancora.customerbookshelf.exception.ExternalBookNotFoundException;
import com.ancora.customerbookshelf.exception.ResourceNotFoundException;
import com.ancora.customerbookshelf.model.Book;
import com.ancora.customerbookshelf.model.Customer;
import com.ancora.customerbookshelf.repository.BookRepository;
import com.ancora.customerbookshelf.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteBookServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FavoriteBookService favoriteBookService;

    private Customer customer;
    private Book book;
    private BookResponseDTO bookResponseDTO;
    private final String ISBN = "123456789";
    private final Long CUSTOMER_ID = 1L;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName("Test Customer");
        customer.setFavoriteBooks(new HashSet<>());

        book = Book.builder().isbn(ISBN).title("Test Book").authors(java.util.Collections.singletonList("Author")).subjects(java.util.Collections.singletonList("Subject")).build();

        bookResponseDTO = new BookResponseDTO();
        bookResponseDTO.setIsbn(ISBN);
        bookResponseDTO.setTitle("Test Book from API");

        // Injeta a URL da API no serviço, já que @Value não funciona em testes unitários puros.
        ReflectionTestUtils.setField(favoriteBookService, "brasilApiUrl", "http://fake.api/");
    }

    @Test
    void testAddBookToFavorites_NewBook_Success() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty()); // Livro não está no DB local
        when(restTemplate.getForObject(anyString(), any())).thenReturn(bookResponseDTO); // API externa retorna o livro
        when(bookRepository.save(any(Book.class))).thenReturn(book); // Salva o novo livro

        // Act
        favoriteBookService.addBookToFavorites(CUSTOMER_ID, ISBN);

        // Assert
        assertTrue(customer.getFavoriteBooks().contains(book));
        verify(restTemplate, times(1)).getForObject(anyString(), any());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testAddBookToFavorites_ExistingBook_Success() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book)); // Livro JÁ ESTÁ no DB local

        // Act
        favoriteBookService.addBookToFavorites(CUSTOMER_ID, ISBN);

        // Assert
        assertTrue(customer.getFavoriteBooks().contains(book));
        verify(restTemplate, never()).getForObject(anyString(), any()); // Verifica que a API externa NÃO foi chamada
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testAddBookToFavorites_CustomerNotFound() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            favoriteBookService.addBookToFavorites(CUSTOMER_ID, ISBN);
        });
    }

    @Test
    void testAddBookToFavorites_AlreadyInFavorites() {
        // Arrange
        customer.getFavoriteBooks().add(book);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(BookAlreadyInFavoritesException.class, () -> {
            favoriteBookService.addBookToFavorites(CUSTOMER_ID, ISBN);
        });
    }

    @Test
    void testAddBookToFavorites_ExternalApiNotFound() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), any())).thenThrow(HttpClientErrorException.NotFound.class);

        // Act & Assert
        assertThrows(ExternalBookNotFoundException.class, () -> {
            favoriteBookService.addBookToFavorites(CUSTOMER_ID, ISBN);
        });
    }

    @Test
    void testRemoveBookFromFavorites_Success() {
        // Arrange
        customer.getFavoriteBooks().add(book);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        // Act
        favoriteBookService.removeBookFromFavorites(CUSTOMER_ID, ISBN);

        // Assert
        assertFalse(customer.getFavoriteBooks().contains(book));
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testRemoveBookFromFavorites_BookNotFoundInList() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer)); // Cliente existe, mas não tem o livro

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            favoriteBookService.removeBookFromFavorites(CUSTOMER_ID, ISBN);
        });
    }

    @Test
    void testGetFavoriteBooks_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        when(bookRepository.findFavoritesByCustomerId(CUSTOMER_ID, pageable)).thenReturn(bookPage);

        // Act
        Page<com.ancora.customerbookshelf.dto.BookDTO> result = favoriteBookService.getFavoriteBooks(CUSTOMER_ID, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(ISBN, result.getContent().get(0).getIsbn());
    }
}
