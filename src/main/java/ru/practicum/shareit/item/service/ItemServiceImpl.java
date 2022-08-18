package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (userStorage.isUserInMemoryById(userId)) {
            Item item = ItemMapper.fromDtoToItem(itemDto, userId, null);
            return ItemMapper.toItemDto(itemStorage.create(item));
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (userStorage.isUserInMemoryById(userId)) {
            Item item = ItemMapper.fromDtoToItem(itemDto, userId, itemId);
            return ItemMapper.toItemDto(itemStorage.update(item));
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getUserItems(long userId) {
        if (userStorage.isUserInMemoryById(userId)) {
            return itemStorage.getUserItems(userId).stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toSet());
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public Collection<ItemDto> findItems(String text) {
        return itemStorage.findItems(text).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toSet());
    }
}
