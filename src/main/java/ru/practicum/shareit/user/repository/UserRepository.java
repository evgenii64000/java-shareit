package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
}
