package com.ancora.customerbookshelf.controller;

import com.ancora.customerbookshelf.dto.BookDTO;
import com.ancora.customerbookshelf.service.FavoriteBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/favorites")
@RequiredArgsConstructor
public class FavoriteBookController {

    private final FavoriteBookService favoriteBookService;

    @GetMapping
    public ResponseEntity<Set<BookDTO>> getFavorites(@PathVariable Long customerId) {
        return ResponseEntity.ok(favoriteBookService.getFavoriteBooks(customerId));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getFavoriteByIsbn(@PathVariable Long customerId, @PathVariable String isbn) {
        return ResponseEntity.ok(favoriteBookService.getFavoriteBookByIsbn(customerId, isbn));
    }

    @PostMapping("/{isbn}")
    public ResponseEntity<Void> addBook(@PathVariable Long customerId, @PathVariable String isbn) {
        favoriteBookService.addBookToFavorites(customerId, isbn);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeBook(@PathVariable Long customerId, @PathVariable String isbn) {
        favoriteBookService.removeBookFromFavorites(customerId, isbn);
        return ResponseEntity.noContent().build();
    }
}
