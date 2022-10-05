package ru.practicum.shareit.requests.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO .
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemRequestDto {

    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
