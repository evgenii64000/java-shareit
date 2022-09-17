package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long id);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?", nativeQuery = true)
    List<Booking> findByOwnerId(Long id);

    List<Booking> findAllByItem_Id(Long id);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.end_time < ?2 ", nativeQuery = true)
    List<Booking> findAllByBookerIdPast(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.start_time > ?2 ", nativeQuery = true)
    List<Booking> findAllByBookerIdFuture(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ", nativeQuery = true)
    List<Booking> findAllByBookerIdCurrent(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.status = ?2 ", nativeQuery = true)
    List<Booking> findAllByBookerIdAndStatus(Long id, String status);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.end_time < ?2 ", nativeQuery = true)
    List<Booking> findAllByOwnerPast(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.start_time > ?2 ", nativeQuery = true)
    List<Booking> findAllByOwnerFuture(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ", nativeQuery = true)
    List<Booking> findAllByOwnerCurrent(Long id, LocalDateTime now);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.status = ?2 ", nativeQuery = true)
    List<Booking> findAllByOwnerStatus(Long id, String status);
}
