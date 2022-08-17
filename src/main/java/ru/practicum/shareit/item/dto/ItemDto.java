package ru.practicum.shareit.item.dto;

import lombok.*;

/**
 * // TODO .
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemDto {

    long id;
    String name;
    String description;
    boolean isAvailable;
}
