package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BookingDtoFront {

    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
