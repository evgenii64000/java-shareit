package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDtoWithBooking getItemById(long itemId, long userId);

    Collection<ItemDtoWithBooking> getUserItems(long userId);

    Collection<ItemDto> findItems(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}