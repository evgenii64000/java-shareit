package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, PagingAndSortingRepository<Booking, Long> {

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book WHERE book.booker_id = ?1",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book WHERE book.booker_id = ?1", nativeQuery = true)
    Page<Booking> findAllByBookerId(Long id, Pageable pageable);

    List<Booking> findAllByBookerId(Long id);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
                    "WHERE item.owner_id = ?", nativeQuery = true)
    Page<Booking> findByOwnerId(Long id, Pageable pageable);

    List<Booking> findAllByItem_Id(Long id);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.end_time < ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "WHERE book.booker_id = ?1 AND book.end_time < ?2 ", nativeQuery = true)
    Page<Booking> findAllByBookerIdPast(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.start_time > ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "WHERE book.booker_id = ?1 AND book.start_time > ?2 ", nativeQuery = true)
    Page<Booking> findAllByBookerIdFuture(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "WHERE book.booker_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ", nativeQuery = true)
    Page<Booking> findAllByBookerIdCurrent(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "WHERE book.booker_id = ?1 AND book.status = ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "WHERE book.booker_id = ?1 AND book.status = ?2 ", nativeQuery = true)
    Page<Booking> findAllByBookerIdAndStatus(Long id, String status, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.end_time < ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
                    "WHERE item.owner_id = ?1 AND book.end_time < ?2 ", nativeQuery = true)
    Page<Booking> findAllByOwnerPast(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.start_time > ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
                    "WHERE item.owner_id = ?1 AND book.start_time > ?2 ", nativeQuery = true)
    Page<Booking> findAllByOwnerFuture(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
                    "WHERE item.owner_id = ?1 AND book.end_time >= ?2 AND book.start_time <= ?2 ", nativeQuery = true)
    Page<Booking> findAllByOwnerCurrent(Long id, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT * FROM SHAREIT.PUBLIC.BOOKING AS book " +
            "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
            "WHERE item.owner_id = ?1 AND book.status = ?2 ",
            countQuery = "SELECT count(*) FROM SHAREIT.PUBLIC.BOOKING AS book " +
                    "LEFT OUTER JOIN SHAREIT.PUBLIC.ITEMS AS item ON book.item_id = item.id  " +
                    "WHERE item.owner_id = ?1 AND book.status = ?2 ", nativeQuery = true)
    Page<Booking> findAllByOwnerStatus(Long id, String status, Pageable pageable);
}
