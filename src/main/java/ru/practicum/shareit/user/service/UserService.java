package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    UserDto getUserById(long id);

    void deleteUserById(long id);

    Collection<UserDto> getAllUsers();
}
