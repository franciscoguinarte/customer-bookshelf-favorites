package com.ancora.customerbookshelf.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AsyncBookProcessorService {


    private final FavoriteBookService favoriteBookService;

    public AsyncBookProcessorService(FavoriteBookService favoriteBookService) {
        this.favoriteBookService = favoriteBookService;
    }

    @Async
    public void processBookAdditions(Long customerId, List<String> isbns) {
        log.info("Starting bulk add process for customer {} with {} books.", customerId, isbns.size());
        for (String isbn : isbns) {
            try {
                favoriteBookService.addBookToFavorites(customerId, isbn);
                log.info("Successfully processed ISBN {} for customer {}", isbn, customerId);
            } catch (Exception e) {
                log.error("Failed to process ISBN {} for customer {}: {}", isbn, customerId, e.getMessage());
            }
        }
        log.info("Finished bulk add process for customer {}.", customerId);
    }
}
