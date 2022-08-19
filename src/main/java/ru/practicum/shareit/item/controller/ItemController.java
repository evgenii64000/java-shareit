package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @RequestBody @Valid ItemDto item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long id,
                             @RequestBody ItemDto item) {
        return itemService.update(userId, id, item);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(@RequestParam String text) {
        return itemService.findItems(text);
    }
}
