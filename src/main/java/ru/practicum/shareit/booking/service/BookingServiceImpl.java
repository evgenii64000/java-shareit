package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto createBooking(BookingDtoFront bookingDtoFront, Long userId) {
        Optional<User> bookerUser = userRepository.findById(userId);
        if (bookerUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User booker = bookerUser.get();
        Optional<Item> bookItem = itemRepository.findById(bookingDtoFront.getItemId());
        if (bookItem.isEmpty()) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        Item item = bookItem.get();
        if (item.getOwner().getId() == userId) {
            throw new WrongUserException("Собственник не может бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Предмет недоступен для бронирования");
        }
        if (bookingDtoFront.getStart().isAfter(bookingDtoFront.getEnd())) {
            throw new WrongTimeException("Время начала бронирования позже времени окончания бронирования");
        }
        if (bookingDtoFront.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongTimeException("Время начала бронирования в прошлом");
        }
        Booking booking = BookingMapper.fromDtoFrontToBooking(bookingDtoFront, item, booker, null);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item, booker);
    }

    @Override
    public BookingDto updateBookingByUser(Long bookingId, Long ownerId, Boolean approved) {
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Optional<Booking> bookingToApprove = bookingRepository.findById(bookingId);
        if (bookingToApprove.isEmpty()) {
            throw new NotFoundException("Такой заявки на бронирование не найдено");
        }
        Booking booking = bookingToApprove.get();
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new WrongUserException("Обновлять статус бронирования может только владелец вещи");
        }
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
            return BookingMapper.toBookingDto(booking, booking.getItem(), booking.getBooker());
        } else {
            throw new WrongStatusException("Бронирование уже подтверждено");
        }
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Optional<User> bookerUser = userRepository.findById(userId);
        if (bookerUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Optional<Booking> bookingToGet = bookingRepository.findById(bookingId);
        if (bookingToGet.isEmpty()) {
            throw new NotFoundException("Такой заявки на бронирование не найдено");
        }
        Booking booking = bookingToGet.get();
        Long ownerIdCheck = booking.getItem().getOwner().getId();
        Long bookerIdCheck = booking.getBooker().getId();
        if (ownerIdCheck != userId && bookerIdCheck != userId) {
            throw new WrongUserException("Получить бронирование может только владелец вещи или создатель заявки");
        }
        return BookingMapper.toBookingDto(booking, booking.getItem(), booking.getBooker());
    }

    @Override
    public Collection<BookingDto> getBookingsForUser(Long userId, String state) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                break;
            case "PAST":
                bookings = bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus().name().equals("WAITING"))
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus().name().equals("REJECTED"))
                        .collect(Collectors.toList());
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking, booking.getItem(), booking.getBooker()))
                .sorted((b1, b2) -> -b1.getStart().toInstant(ZoneOffset.UTC).compareTo(b2.getStart().toInstant(ZoneOffset.UTC)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Collection<BookingDto> getBookingsForOwner(Long userId, String state) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        List<Booking> bookings = bookingRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                break;
            case "PAST":
                bookings = bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(now))
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now))
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus().name().equals("WAITING"))
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus().name().equals("REJECTED"))
                        .collect(Collectors.toList());
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(booking -> BookingMapper.toBookingDto(booking, booking.getItem(), booking.getBooker()))
                .sorted((b1, b2) -> -b1.getStart().toInstant(ZoneOffset.UTC).compareTo(b2.getStart().toInstant(ZoneOffset.UTC)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
