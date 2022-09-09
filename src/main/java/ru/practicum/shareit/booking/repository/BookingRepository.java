package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long id);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?", nativeQuery = true)
    List<Booking> findByOwnerId(Long id);

    List<Booking> findAllByItem_Id(Long id);
}
