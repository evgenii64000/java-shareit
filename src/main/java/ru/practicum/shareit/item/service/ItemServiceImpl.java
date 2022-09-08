package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        Optional<User> userOwner = userRepository.findById(userId);
        if (userOwner.isPresent()) {
            Item item = ItemMapper.fromDtoToItem(itemDto, userOwner.get(), null);
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
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
    public ItemDto getItemById(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toItemDto(item.get());
        } else {
            throw new NotFoundException("Предмет с таким id не найден");
        }
    }

    @Override
    public Collection<ItemDto> getUserItems(long userId) {
        Optional<User> userOwner = userRepository.findById(userId);
        if (userOwner.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User owner = userOwner.get();
        return itemRepository.findAllByOwner(userOwner.get()).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByNameAndDescription(text, text).stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toSet());
        }
    }
}
