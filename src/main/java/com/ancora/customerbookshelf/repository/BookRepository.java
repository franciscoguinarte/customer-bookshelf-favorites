package com.ancora.customerbookshelf.repository;

import com.ancora.customerbookshelf.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("SELECT b FROM Customer c JOIN c.favoriteBooks b WHERE c.id = :customerId")
    Page<Book> findFavoritesByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
