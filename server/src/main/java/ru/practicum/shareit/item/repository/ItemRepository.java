package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, PagingAndSortingRepository<Item, Long> {

    Page<Item> findAllByOwner(User owner, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.ITEMS " +
            "WHERE (LOWER(items.name) like LOWER(CONCAT('%', ?1, '%')) OR LOWER(items.description) like LOWER(CONCAT('%', ?2, '%'))) " +
            "AND items.available = true", nativeQuery = true)
    Page<Item> findByNameAndDescription(String text1, String text2, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.ITEMS " +
            "WHERE items.request_id = ?", nativeQuery = true)
    List<Item> getAnswers(Long requestId);
}
