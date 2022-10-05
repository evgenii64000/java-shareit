package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.BookingInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private JacksonTester<BookingInfo> jsonInfo;

    @Autowired
    private JacksonTester<CommentDto> jsonComment;

    @Autowired
    private JacksonTester<ItemDtoWithBooking> jsonAll;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testBookingInfo() throws Exception {
        BookingInfo bookingInfo = BookingInfo.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookingInfo> result = jsonInfo.write(bookingInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("test")
                .authorName("test")
                .created(LocalDateTime.now().withNano(0))
                .build();

        JsonContent<CommentDto> result = jsonComment.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().toString());
    }

    @Test
    void testItemDtoWithBooking() throws Exception {
        BookingInfo bookingInfoLast = BookingInfo.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        BookingInfo bookingInfoNext = BookingInfo.builder()
                .id(2L)
                .bookerId(2L)
                .build();

        CommentDto commentDto1 = CommentDto.builder()
                .id(1L)
                .text("test")
                .authorName("test")
                .created(LocalDateTime.now().withNano(0))
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .id(2L)
                .text("test2")
                .authorName("test2")
                .created(LocalDateTime.now().withNano(0))
                .build();

        ItemDtoWithBooking itemDtoWithBooking = ItemDtoWithBooking.builder()
                .id(1L)
                .name("test")
                .description("description")
                .available(true)
                .lastBooking(bookingInfoLast)
                .nextBooking(bookingInfoNext)
                .comments(List.of(commentDto1, commentDto2))
                .build();

        JsonContent<ItemDtoWithBooking> result = jsonAll.write(itemDtoWithBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);
    }
}
