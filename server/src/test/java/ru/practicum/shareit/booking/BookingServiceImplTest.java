package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;

    private BookingDto bookingDto;

    private BookingDtoFront bookingDtoFront = BookingDtoFront.builder()
            .start(LocalDateTime.now().plusSeconds(1L))
            .end(LocalDateTime.now().plusSeconds(2L))
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .name("test")
            .description("testDescription")
            .available(true)
            .requestId(null)
            .build();

    private UserDto owner = UserDto.builder()
            .name("owner")
            .email("owner@mail")
            .build();

    private UserDto booker = UserDto.builder()
            .name("booker")
            .email("booker@mail")
            .build();

    @Test
    public void testCreateBooking() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query
                .setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(booking.getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testCreateBookingWrongUser() {
        try {
            bookingDto = bookingService.createBooking(bookingDtoFront, 200L);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testCreateBookingWrongItem() {
        try {
            booker = userService.create(booker);
            bookingDtoFront.setItemId(200L);
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Предмет с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testCreateBookingByOwner() {
        try {
            owner = userService.create(owner);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, owner.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Собственник не может бронировать свою вещь", e.getMessage());
        }
    }

    @Test
    public void testCreateBookingUnavailable() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto.setAvailable(false);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Предмет недоступен для бронирования", e.getMessage());
        }
    }

    @Test
    public void testCreateBookingWrongEnd() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDtoFront.setEnd(bookingDtoFront.getEnd().minusDays(2L));
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Время начала бронирования позже времени окончания бронирования", e.getMessage());
        }
    }

    @Test
    public void testCreateBookingWrongStart() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDtoFront.setStart(bookingDtoFront.getStart().minusDays(2L));
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Время начала бронирования в прошлом", e.getMessage());
        }
    }

    @Test
    public void testUpdateBookingByUser() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        BookingDto bookingDtoGet = bookingService.updateBookingByUser(bookingDto.getId(), owner.getId(), true);
        bookingDto.setStatus(BookingStatus.APPROVED);
        assertThat(bookingDtoGet.getId(), notNullValue());
        assertThat(bookingDtoGet.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingDtoGet.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoGet.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoGet.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingDtoGet.getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testUpdateBookingWrongUser() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.updateBookingByUser(bookingDto.getId(), 200L, true);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateBookingWrongBooking() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.updateBookingByUser(200L, owner.getId(), true);
        } catch (Exception e) {
            Assertions.assertEquals("Такой заявки на бронирование не найдено", e.getMessage());
        }
    }

    @Test
    public void testUpdateBookingWrongOwner() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.updateBookingByUser(bookingDto.getId(), booker.getId(), true);
        } catch (Exception e) {
            Assertions.assertEquals("Обновлять статус бронирования может только владелец вещи", e.getMessage());
        }
    }

    @Test
    public void testUpdateBookingAlreadyUpdate() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.updateBookingByUser(bookingDto.getId(), owner.getId(), true);
            bookingService.updateBookingByUser(bookingDto.getId(), owner.getId(), true);
        } catch (Exception e) {
            Assertions.assertEquals("Бронирование уже подтверждено", e.getMessage());
        }
    }

    @Test
    public void testGetBooking() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        BookingDto bookingDtoGet = bookingService.getBooking(owner.getId(), bookingDto.getId());
        assertThat(bookingDtoGet.getId(), equalTo(bookingDto.getId()));
        assertThat(bookingDtoGet.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingDtoGet.getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingDtoGet.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingDtoGet.getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingDtoGet.getItem(), equalTo(bookingDto.getItem()));
    }


    @Test
    public void testGetBookingWrongUser() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBooking(200L, bookingDto.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetBookingWrongBooking() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBooking(owner.getId(), 200L);
        } catch (Exception e) {
            Assertions.assertEquals("Такой заявки на бронирование не найдено", e.getMessage());
        }
    }

    @Test
    public void testGetBookingAnotherUser() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            booker.setName("another");
            booker.setEmail("another@mail");
            UserDto another = userService.create(booker);
            bookingService.getBooking(another.getId(), bookingDto.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Получить бронирование может только владелец вещи или создатель заявки", e.getMessage());
        }
    }

    @Test
    public void testGetBookingsForUserPast() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForUser(booker.getId(), "PAST", 0, 5));
        Assertions.assertEquals(bookingsGet, Collections.emptyList());
    }

    @Test
    public void testGetBookingsForUserCurrent() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForUser(booker.getId(), "CURRENT", 0, 5));
        Assertions.assertEquals(bookingsGet, Collections.emptyList());
    }

    @Test
    public void testGetBookingsForUserFuture() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForUser(booker.getId(), "FUTURE", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForUserAll() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForUser(booker.getId(), "ALL", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForUserWaiting() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForUser(booker.getId(), "WAITING", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForUserWrongUser() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBookingsForUser(200L, "ALL", 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetBookingsForUserUnknownState() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBookingsForUser(booker.getId(), "unknown", 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Unknown state: unknown", e.getMessage());
        }
    }

    @Test
    public void testGetBookingsForOwnerPast() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForOwner(owner.getId(), "PAST", 0, 5));
        Assertions.assertEquals(bookingsGet, Collections.emptyList());
    }

    @Test
    public void testGetBookingsForOwnerCurrent() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForOwner(owner.getId(), "CURRENT", 0, 5));
        Assertions.assertEquals(bookingsGet, Collections.emptyList());
    }

    @Test
    public void testGetBookingsForOwnerFuture() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForOwner(owner.getId(), "FUTURE", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForOwnerAll() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForOwner(owner.getId(), "ALL", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForOwnerWaiting() {
        owner = userService.create(owner);
        booker = userService.create(booker);
        itemDto = itemService.create(owner.getId(), itemDto);
        bookingDtoFront.setItemId(itemDto.getId());
        bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
        List<BookingDto> bookingsGet = new ArrayList<>(bookingService.getBookingsForOwner(owner.getId(), "WAITING", 0, 5));
        assertThat(bookingsGet, hasSize(1));
        assertThat(bookingsGet.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(bookingsGet.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(bookingsGet.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(bookingsGet.get(0).getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(bookingsGet.get(0).getBooker(), equalTo(bookingDto.getBooker()));
        assertThat(bookingsGet.get(0).getItem(), equalTo(bookingDto.getItem()));
    }

    @Test
    public void testGetBookingsForOwnerWrongUser() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBookingsForOwner(200L, "ALL", 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetBookingsForOwnerUnknownState() {
        try {
            owner = userService.create(owner);
            booker = userService.create(booker);
            itemDto = itemService.create(owner.getId(), itemDto);
            bookingDtoFront.setItemId(itemDto.getId());
            bookingDto = bookingService.createBooking(bookingDtoFront, booker.getId());
            bookingService.getBookingsForOwner(owner.getId(), "unknown", 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Unknown state: unknown", e.getMessage());
        }
    }
}
