package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.Valid;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto postBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchOwnerBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        return bookingService.updateBookingByUser(bookingId, userId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long id) {
        return bookingService.getBooking(userId, id);
    }

    @GetMapping("/bookings")
    public Collection<BookingDto> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsForUser(userId, state);
    }

    @GetMapping("/bookings/owner")
    public Collection<BookingDto> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsForOwner(userId, state);
    }
}
