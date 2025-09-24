package com.ancora.customerbookshelf.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "book")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @Column(length = 20)
    private String isbn;

    private String title;

    private String subtitle;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "author")
    private List<String> authors;

    private String publisher;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Embedded
    private Dimensions dimensions;

    private Integer year;

    private String format;

    private Integer pageCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_isbn"))
    @Column(name = "subject")
    private List<String> subjects;

    private String location;

    private Double retailPrice;

    private String coverUrl;

    private String provider;
}