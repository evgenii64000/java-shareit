package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Optional<User> userOwner = userRepository.findById(userId);
        if (userOwner.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User owner = userOwner.get();
        if (itemDto.getRequestId() == null) {
            Item item = ItemMapper.fromDtoToItem(itemDto, owner, null, null);
            ItemDto itemDtoToReturn = ItemMapper.toItemDto(itemRepository.save(item));
            itemDtoToReturn.setRequestId(null);
            return itemDtoToReturn;
        } else {
            Optional<ItemRequest> itemRequestOptional = requestRepository.findById(itemDto.getRequestId());
            if (itemRequestOptional.isEmpty()) {
                throw new NotFoundException("Запрос с таким id не найден");
            }
            ItemRequest request = itemRequestOptional.get();
            Item item = ItemMapper.fromDtoToItem(itemDto, owner, null, request);
            ItemDto itemDtoToReturn = ItemMapper.toItemDto(itemRepository.save(item));
            itemDtoToReturn.setRequestId(request.getId());
            return itemDtoToReturn;
        }
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Optional<User> userOwner = userRepository.findById(userId);
        if (userOwner.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Optional<Item> itemToUpdate = itemRepository.findById(itemId);
        if (itemToUpdate.isEmpty()) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        Item item = itemToUpdate.get();
        if (!userOwner.get().getId().equals(item.getOwner().getId())) {
            throw new WrongIdException("Неверный id пользователя");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithBooking getItemById(long itemId, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Optional<Item> itemToGet = itemRepository.findById(itemId);
        if (itemToGet.isEmpty()) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        Item item = itemToGet.get();
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentDtos;
        if (comments.isEmpty()) {
            commentDtos = Collections.emptyList();
        } else {
            commentDtos = comments.stream()
                    .map(comment -> CommentMapper.toCommentDto(comment))
                    .collect(Collectors.toList());
        }
        if (item.getOwner().getId() != userId) {
            return ItemMapper.toItemDtoWithBooking(item, null, null, commentDtos);
        } else {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
            Optional<Booking> next = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .findFirst();
            BookingInfo nextInfo = ItemMapper.toBookingInfo(next);
            Optional<Booking> last = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .findFirst();
            BookingInfo lastInfo = ItemMapper.toBookingInfo(last);
            return ItemMapper.toItemDtoWithBooking(item, nextInfo, lastInfo, commentDtos);
        }
    }

    @Override
    public Collection<ItemDtoWithBooking> getUserItems(long userId, Integer from, Integer size) {
        Optional<User> userOwner = userRepository.findById(userId);
        if (userOwner.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User owner = userOwner.get();
        if (from < 0 || size < 0) {
            throw new WrongParameterException("Неправильные параметры запроса");
        }
        LocalDateTime now = LocalDateTime.now();
        Page<Item> items = itemRepository.findAllByOwner(owner, PageRequest.of(from / size, size));
        List<ItemDtoWithBooking> result = new ArrayList<>();
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItem_Id(item.getId());
            Optional<Booking> next = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .findFirst();
            BookingInfo nextInfo = ItemMapper.toBookingInfo(next);
            Optional<Booking> last = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(now))
                    .findFirst();
            BookingInfo lastInfo = ItemMapper.toBookingInfo(last);
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<CommentDto> commentDtos;
            if (comments.isEmpty()) {
                commentDtos = Collections.emptyList();
            } else {
                commentDtos = comments.stream()
                        .map(comment -> CommentMapper.toCommentDto(comment))
                        .collect(Collectors.toList());
            }
            result.add(ItemMapper.toItemDtoWithBooking(item, nextInfo, lastInfo, commentDtos));
        }
        return result;
    }

    @Override
    public Collection<ItemDto> findItems(String text, Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new WrongParameterException("Неправильные параметры запроса");
        }
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByNameAndDescription(text, text, PageRequest.of(from / size, size)).stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Optional<User> bookerUser = userRepository.findById(userId);
        if (bookerUser.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User booker = bookerUser.get();
        Optional<Item> itemComment = itemRepository.findById(itemId);
        if (itemComment.isEmpty()) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        Item item = itemComment.get();
        List<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId());
        Optional<Booking> booking = bookings.stream()
                .findFirst().filter(book -> book.getItem().getId().equals(itemId));
        if (booking.isEmpty()) {
            throw new WrongUserException("Пользователь не бронировал эту вещь");
        }
        if (booking.get().getStart().isAfter(LocalDateTime.now())) {
            throw new WrongTimeException("Пользователь ещё не пользовался этой вещью");
        }
        Comment comment = CommentMapper.fromDtoToComment(commentDto, item, booker);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
