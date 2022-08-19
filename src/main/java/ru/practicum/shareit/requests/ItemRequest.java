package ru.practicum.shareit.requests;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemRequest {

    long id;
    String name;
    User requestor;
    LocalDateTime created;
}
