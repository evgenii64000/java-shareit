package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

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
                            @RequestBody ItemDto item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable long id,
                             @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBooking getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long id) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public Collection<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(@RequestParam String text,
                                         @RequestParam Integer from,
                                         @RequestParam Integer size) {
        return itemService.findItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}