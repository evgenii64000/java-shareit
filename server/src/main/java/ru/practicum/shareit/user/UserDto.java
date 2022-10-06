package ru.practicum.shareit.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")
public class UserDto {

    private Long id;
    private String name;
    private String email;
}
