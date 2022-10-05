package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto);

    Collection<ItemRequestDto> getRequests(Long userId);

    Collection<ItemRequestDto> getUsersRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequest(Long userId, Long requestId);
}
