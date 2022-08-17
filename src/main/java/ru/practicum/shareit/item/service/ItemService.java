package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto create(long userId, Item item);

    ItemDto update(long userId, long itemId, Item item);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getUserItems(long userId);

    Collection<ItemDto> findItems(String text);
}