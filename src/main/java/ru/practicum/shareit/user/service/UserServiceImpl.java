package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.fromDtoToUser(userDto, null);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        Optional<User> userToUpdate = userRepository.findById(id);
        if (userToUpdate.isPresent()) {
            User user = userToUpdate.get();
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            return UserMapper.toUserDto(userRepository.save(user));
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
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
