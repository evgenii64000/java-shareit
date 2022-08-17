package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OccupiedException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private final HashSet<String> emails = new HashSet<>();
    private long idCounter = 0;

    private long generateId() {
        idCounter++;
        return idCounter;
    }

    @Override
    public boolean isUserInMemoryById(long userId) {
        if (users.containsKey(userId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new OccupiedException("Такой email уже существует");
        } else {
            user.setId(generateId());
            users.put(user.getId(), user);
            emails.add(user.getEmail());
            return user;
        }
    }

    @Override
    public User getUserById(long id) {
        if (isUserInMemoryById(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public  User update(User user, long id) {
        if (isUserInMemoryById(id)) {
            if (user.getEmail() != null) {
                updateUserEmail(user, id);
            }
            if (user.getName() != null) {
                updateUserName(user, id);
            }
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    private void updateUserEmail(User user, long id) {
        String oldEmail = users.get(id).getEmail();
        String newEmail = user.getEmail();
        if (!oldEmail.equals(newEmail)) {
            if (emails.contains(newEmail)) {
                throw new OccupiedException("Такой email уже существует");
            } else {
                emails.remove(oldEmail);
                users.get(id).setEmail(newEmail);
                emails.add(newEmail);
            }
        }
    }

    private void updateUserName(User user, long id) {
        String oldName = users.get(id).getName();
        String newName = user.getName();
        if (!oldName.equals(newName)) {
            users.get(id).setName(newName);
        }
    }

    @Override
    public void deleteUserById(long id) {
        if (isUserInMemoryById(id)) {
            emails.remove(users.get(id).getEmail());
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }
}
