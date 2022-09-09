package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item fromDtoToItem(ItemDto itemDto, User owner, Long id) {
        return Item.builder()
                .id(id)
                .owner(owner)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, BookingInfo next, BookingInfo last, List<CommentDto> commentDtos) {
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(next)
                .lastBooking(last)
                .comments(commentDtos)
                .build();
    }

    public static BookingInfo toBookingInfo(Optional<Booking> book) {
        if (book.isPresent()) {
            return BookingInfo.builder()
                    .id(book.get().getId())
                    .bookerId(book.get().getBooker().getId())
                    .build();
        } else {
            return null;
        }
    }
}
