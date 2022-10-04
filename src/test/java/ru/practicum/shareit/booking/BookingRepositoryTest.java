package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private List<Booking> bookings;

    private User owner = User.builder()
            .name("owner")
            .email("owner@mail")
            .build();

    private User booker = User.builder()
            .name("booker")
            .email("booker@mail")
            .build();

    private Item item1 = Item.builder()
            .name("test")
            .description("this is description")
            .available(true)
            .build();

    private Item item2 = Item.builder()
            .name("another")
            .description("another description")
            .available(true)
            .build();

    private Item item3 = Item.builder()
            .name("name")
            .description("just description")
            .available(true)
            .build();

    private Booking booking1 = Booking.builder()
            .start(LocalDateTime.now().minusDays(3L))
            .end(LocalDateTime.now().minusDays(2L))
            .status(BookingStatus.APPROVED)
            .build();

    private Booking booking2 = Booking.builder()
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .status(BookingStatus.REJECTED)
            .build();

    private Booking booking3 = Booking.builder()
            .start(LocalDateTime.now().plusDays(2L))
            .end(LocalDateTime.now().plusDays(3L))
            .status(BookingStatus.WAITING)
            .build();

    private void preparation() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        item3.setOwner(owner);
        item3 = itemRepository.save(item3);

        booking1.setBooker(booker);
        booking1.setItem(item1);
        booking1 = bookingRepository.save(booking1);

        booking2.setBooker(booker);
        booking2.setItem(item2);
        booking2 = bookingRepository.save(booking2);

        booking3.setBooker(booker);
        booking3.setItem(item3);
        booking3 = bookingRepository.save(booking3);
    }

    @Test
    public void testFindAllByBookerId() {
        preparation();
        bookings = bookingRepository.findAllByBookerId(booker.getId());
        Assertions.assertEquals(3, bookings.size());
        Assertions.assertEquals(booking1, bookings.get(0));
        Assertions.assertEquals(booking2, bookings.get(1));
        Assertions.assertEquals(booking3, bookings.get(2));

    }

    @Test
    public void testFindByOwnerId() {
        preparation();
        bookings = bookingRepository.findByOwnerId(owner.getId(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(3, bookings.size());
        Assertions.assertEquals(booking3, bookings.get(0));
        Assertions.assertEquals(booking2, bookings.get(1));
        Assertions.assertEquals(booking1, bookings.get(2));
    }

    @Test
    public void testFindAllByBookerIdPast() {
        preparation();
        bookings = bookingRepository.findAllByBookerIdPast(booker.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void testFindAllByBookerIdFuture() {
        preparation();
        bookings = bookingRepository.findAllByBookerIdFuture(booker.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking3, bookings.get(0));
    }

    @Test
    public void testFindAllByBookerIdCurrent() {
        preparation();
        bookings = bookingRepository.findAllByBookerIdCurrent(booker.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking2, bookings.get(0));
    }

    @Test
    public void testFindAllByBookerIdAndStatus() {
        preparation();
        bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), "WAITING", PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking3, bookings.get(0));
    }

    @Test
    public void testFindAllByOwnerPast() {
        preparation();
        bookings = bookingRepository.findAllByOwnerPast(owner.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking1, bookings.get(0));
    }

    @Test
    public void testFindAllByOwnerFuture() {
        preparation();
        bookings = bookingRepository.findAllByOwnerFuture(owner.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking3, bookings.get(0));
    }

    @Test
    public void testFindAllByOwnerCurrent() {
        preparation();
        bookings = bookingRepository.findAllByOwnerCurrent(owner.getId(), LocalDateTime.now(), PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking2, bookings.get(0));
    }

    @Test
    public void testFindAllByOwnerStatus() {
        preparation();
        bookings = bookingRepository.findAllByOwnerStatus(owner.getId(), "REJECTED", PageRequest.of(0 / 20, 20, Sort.by("start_time").descending()))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking2, bookings.get(0));
    }
}
