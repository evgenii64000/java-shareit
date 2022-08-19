package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto, null);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.fromDtoToUser(userDto, id);
        return UserMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }
}
