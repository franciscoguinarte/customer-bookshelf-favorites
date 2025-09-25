package com.ancora.customerbookshelf.controller;

import com.ancora.customerbookshelf.dto.BookDTO;
import com.ancora.customerbookshelf.dto.BulkAddRequestDTO;
import com.ancora.customerbookshelf.service.AsyncBookProcessorService;
import com.ancora.customerbookshelf.service.FavoriteBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/customers/{customerId}/favorites")
@RequiredArgsConstructor
@Slf4j
public class FavoriteBookController {

    private final FavoriteBookService favoriteBookService;
    private final AsyncBookProcessorService asyncBookProcessorService;

    @PostMapping("/bulk-add")
    public ResponseEntity<Void> bulkAddBooks(@PathVariable Long customerId, @RequestBody BulkAddRequestDTO request) {
        log.info("Received bulk add request for customer {} with {} books", customerId, request.getIsbns().size());
        asyncBookProcessorService.processBookAdditions(customerId, request.getIsbns());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<Page<BookDTO>> getFavorites(@PathVariable Long customerId, @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to get all favorite books for customer {}", customerId);
        return ResponseEntity.ok(favoriteBookService.getFavoriteBooks(customerId, pageable));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getFavoriteByIsbn(@PathVariable Long customerId, @PathVariable String isbn) {
        log.info("Received request to get favorite book with isbn {} for customer {}", isbn, customerId);
        return ResponseEntity.ok(favoriteBookService.getFavoriteBookByIsbn(customerId, isbn));
    }

    @PostMapping("/{isbn}")
    public ResponseEntity<Void> addBook(@PathVariable Long customerId, @PathVariable String isbn) {
        log.info("Received request to add book with isbn {} to favorites for customer {}", isbn, customerId);
        favoriteBookService.addBookToFavorites(customerId, isbn);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeBook(@PathVariable Long customerId, @PathVariable String isbn) {
        log.info("Received request to remove book with isbn {} from favorites for customer {}", isbn, customerId);
        favoriteBookService.removeBookFromFavorites(customerId, isbn);
        return ResponseEntity.noContent().build();
    }
}
