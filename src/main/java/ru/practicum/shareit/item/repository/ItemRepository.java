package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(User owner);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.ITEMS " +
            "WHERE (LOWER(items.name) like LOWER(CONCAT('%', ?1, '%')) OR LOWER(items.description) like LOWER(CONCAT('%', ?2, '%'))) " +
            "AND items.available = true", nativeQuery = true)
    List<Item> findByNameAndDescription(String text1, String text2);
}
