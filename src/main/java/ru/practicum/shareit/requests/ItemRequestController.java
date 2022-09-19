package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;

    @Autowired
    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid ItemRequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }
    @GetMapping("/all")
    public Collection<ItemRequestDto> getUsersRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "20") Integer size) {
        return requestService.getUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long requestId) {
        return requestService.getRequest(userId, requestId);
    }
}
