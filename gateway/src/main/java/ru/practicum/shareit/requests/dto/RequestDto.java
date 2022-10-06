package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemRequestDto> items;
}
