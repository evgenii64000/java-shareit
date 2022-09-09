package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking, Item item, User booker) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booker)
                .item(item)
                .build();
    }

    public static Booking fromDtoFrontToBooking(BookingDtoFront bookingDtoFront, Item item, User booker, Long id) {
        return Booking.builder()
                .id(id)
                .start(bookingDtoFront.getStart())
                .end(bookingDtoFront.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }
}
