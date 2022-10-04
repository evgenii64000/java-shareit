package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("test1")
                .description("description1")
                .available(true)
                .requestId(1L)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("test2")
                .description("description2")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("test")
                .created(LocalDateTime.now().withNano(0))
                .items(List.of(itemDto1, itemDto2))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("test1");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description1");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("test2");
        assertThat(result).extractingJsonPathStringValue("$.items[1].description").isEqualTo("description2");
        assertThat(result).extractingJsonPathBooleanValue("$.items[1].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].requestId").isEqualTo(1);
    }
}
