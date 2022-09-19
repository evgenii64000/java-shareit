package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        Optional<User> userRequestor = userRepository.findById(userId);
        if (userRequestor.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User requestor = userRequestor.get();
        LocalDateTime now = LocalDateTime.now();
        ItemRequest newRequest = ItemRequestMapper.fromDtoToRequest(requestDto, null, requestor, now);
        return ItemRequestMapper.toDtoRequest(requestRepository.save(newRequest));
    }

    @Override
    public Collection<ItemRequestDto> getRequests(Long userId) {
        Optional<User> userRequestor = userRepository.findById(userId);
        if (userRequestor.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User requestor = userRequestor.get();
        List<ItemRequest> requests = requestRepository.findAllByRequestorOrderByCreated(requestor);
        List<ItemRequestDto> requestsDto = requests.stream()
                .map(request -> ItemRequestMapper.toDtoRequest(request))
                .collect(Collectors.toList());
        for (ItemRequestDto requestDto : requestsDto) {
            getAnswers(requestDto);
        }
        return requestsDto;
    }

    @Override
    public Collection<ItemRequestDto> getUsersRequests(Long userId, Integer from, Integer size) {
        Optional<User> userRequestor = userRepository.findById(userId);
        if (userRequestor.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        User user = userRequestor.get();
        if (from < 0 || size < 0) {
            throw new WrongParameterException("Неправильные параметры запроса");
        }
        return requestRepository.findAllByRequestorNot(user, PageRequest.of(from, size, Sort.by("created"))).stream()
                .map(request -> ItemRequestMapper.toDtoRequest(request))
                .peek(requestDto -> getAnswers(requestDto))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        Optional<User> userRequestor = userRepository.findById(userId);
        if (userRequestor.isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        Optional<ItemRequest> requestOptional = requestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new NotFoundException("Запрос с таким id не найден");
        }
        ItemRequest request = requestOptional.get();
        ItemRequestDto requestDto = ItemRequestMapper.toDtoRequest(request);
        return getAnswers(requestDto);
    }

    private ItemRequestDto getAnswers(ItemRequestDto requestDto) {
        List<Item> items = itemRepository.getAnswers(requestDto.getId());
        List<ItemDto> answers = items.stream()
                .map(item -> ItemMapper.toItemDto(item))
                .peek(itemDto -> itemDto.setRequestId(requestDto.getId()))
                .collect(Collectors.toList());
        requestDto.setItems(answers);
        return requestDto;
    }
}
