package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService{

    public UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User user, long id) {
        return userStorage.update(user, id);
    }

    @Override
    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
