package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User getUserById(long id);

    void deleteUserById(long id);

    Collection<User> getAllUsers();

    boolean isUserInMemoryById(long id);
}
