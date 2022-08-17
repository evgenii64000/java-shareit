package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    Item getItemById(long itemId);

    Collection<Item> getUserItems(long userId);

    Collection<Item> findItems(String text);
}
