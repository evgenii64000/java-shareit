package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto updateBookingByUser(Long bookingId, Long ownerId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getBookingsForUser(Long userId, String state);

    Collection<BookingDto> getBookingsForOwner(Long userId, String state);
}
