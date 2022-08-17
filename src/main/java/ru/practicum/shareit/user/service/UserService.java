package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    User update(User user, long id);

    User getUserById(long id);

    void deleteUserById(long id);

    Collection<User> getAllUsers();
}
