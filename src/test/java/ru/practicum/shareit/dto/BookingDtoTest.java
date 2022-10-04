package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Autowired
    private JacksonTester<BookingDtoFront> jsonFront;

    @Test
    void testBookingDto() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("test")
                .email("test@mail")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("test")
                .description("test")
                .available(true)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().withNano(0))
                .end(LocalDateTime.now().withNano(0).plusSeconds(20))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("test@mail");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("test");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testBookingDtoFront() throws Exception {
        BookingDtoFront bookingDtoFront = BookingDtoFront.builder()
                .start(LocalDateTime.now().withNano(0))
                .end(LocalDateTime.now().withNano(0).plusSeconds(20))
                .itemId(1L)
                .build();

        JsonContent<BookingDtoFront> result = jsonFront.write(bookingDtoFront);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDtoFront.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDtoFront.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}
