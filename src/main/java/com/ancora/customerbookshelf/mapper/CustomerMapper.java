package com.ancora.customerbookshelf.mapper;

import com.ancora.customerbookshelf.dto.CustomerDTO;
import com.ancora.customerbookshelf.dto.FavoritesSummaryDTO;
import com.ancora.customerbookshelf.model.Book;
import com.ancora.customerbookshelf.model.Customer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomerMapper {

    public static CustomerDTO ToDTO(Customer customer){
        FavoritesSummaryDTO summary = calculateSummary(customer.getFavoriteBooks());

        return CustomerDTO.builder()
                .cpf(customer.getCpf())
                .email(customer.getEmail())
                .name(customer.getName())
                .createdAt(customer.getCreatedAt())
                .favoritesSummary(summary)
                .build();
    }

    public static Customer toEntity(CustomerDTO customerDTO){
        return Customer.builder()
                .cpf(customerDTO.getCpf())
                .email(customerDTO.getEmail())
                .name(customerDTO.getName())
                .build();
    }

    private static FavoritesSummaryDTO calculateSummary(java.util.Set<Book> books) {
        if (books == null || books.isEmpty()) {
            return FavoritesSummaryDTO.builder().count(0).
                    mostFrequentAuthors(Collections.emptyList()).mostFrequentThemes(Collections.emptyList()).build();
        }

        List<String> allAuthors = books.stream()
                .flatMap(book -> book.getAuthors().stream())
                .collect(Collectors.toList());

        List<String> allSubjects = books.stream()
                .flatMap(book -> book.getSubjects().stream())
                .collect(Collectors.toList());

        List<String> mostFrequentAuthors = findMostFrequent(allAuthors);
        List<String> mostFrequentThemes = findMostFrequent(allSubjects);

        return FavoritesSummaryDTO.builder()
                .count(books.size())
                .mostFrequentAuthors(mostFrequentAuthors)
                .mostFrequentThemes(mostFrequentThemes)
                .build();
    }

    private static <T> List<T> findMostFrequent(List<T> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        Map<T, Long> frequencyMap = list.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long maxFrequency = Collections.max(frequencyMap.values());

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
