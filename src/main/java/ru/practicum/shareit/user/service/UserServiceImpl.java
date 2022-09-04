package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto, null);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = UserMapper.fromDtoToUser(userDto, id);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }
}
