package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long idCounter = 0;

    private long generateId() {
        idCounter++;
        return idCounter;
    }

    @Override
    public Item create(long userId, Item item) {
        item.setId(generateId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет с таким id не найден");
        }
        if (items.get(itemId).getOwnerId() != userId) {
            throw new WrongIdException("Неверный id пользователя");
        }
        if (item.getName() != null) {
            String newName = item.getName();
            items.get(itemId).setName(newName);
        }
        if (item.getDescription() != null) {
            String newDescription = item.getDescription();
            items.get(itemId).setDescription(newDescription);
        }
        if (item.getAvailable() != null) {
            Boolean newStatus = item.getAvailable();
            items.get(itemId).setAvailable(newStatus);
        }
        return items.get(itemId);
    }

    @Override
    public Item getItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Предмет с таким id не найден");
        } else {
            return items.get(itemId);
        }
    }

    @Override
    public Collection<Item> getUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Item> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String formattedText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(formattedText)
                        || item.getDescription().toLowerCase().contains(formattedText))
                .filter(item -> item.getAvailable())
                .collect(Collectors.toSet());
    }
}
