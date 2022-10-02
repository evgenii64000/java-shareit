package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                  @RequestBody @Valid BookingDtoFront bookingDtoFront) {
        return bookingService.createBooking(bookingDtoFront, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchOwnerBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        return bookingService.updateBookingByUser(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> getBookingsForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "20") @Positive Integer size) {
        return bookingService.getBookingsForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "20") @Positive Integer size) {
        return bookingService.getBookingsForOwner(userId, state, from, size);
    }
}
