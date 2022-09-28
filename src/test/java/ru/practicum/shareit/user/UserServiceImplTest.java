package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final UserService userService;
    private final EntityManager entityManager;

    @Test
    public void testCreate() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@mail")
                .build();
        userService.create(userDto);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void testGetUserById() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@mail")
                .build();
        userDto = userService.create(userDto);

        UserDto userGet = userService.getUserById(userDto.getId());
        assertThat(userGet.getId(), notNullValue());
        assertThat(userGet.getName(), equalTo(userDto.getName()));
        assertThat(userGet.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void testGetUserByWrongId() {
        try {
            userService.getUserById(1L);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testDeleteUserById() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@mail")
                .build();
        userDto = userService.create(userDto);
        userService.deleteUserById(userDto.getId());
        try {
            userService.getUserById(1L);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetAllUsers() {
        UserDto userDto1 = UserDto.builder()
                .name("test1")
                .email("test1@mail")
                .build();
        UserDto userDto2 = UserDto.builder()
                .name("test2")
                .email("test2@mail")
                .build();
        userService.create(userDto1);
        userService.create(userDto2);
        Assertions.assertEquals(List.of(userDto1, userDto2), userService.getAllUsers());
    }

    @Test
    public void testUpdate() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@mail")
                .build();
        userDto = userService.create(userDto);
        UserDto newUserDto = UserDto.builder()
                .name("newtest")
                .email("newtest@mail")
                .build();
        UserDto userGet = userService.update(newUserDto, userDto.getId());
        assertThat(userGet.getId(), notNullValue());
        assertThat(userGet.getName(), equalTo(newUserDto.getName()));
        assertThat(userGet.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    public void testUpdateWrongId() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@mail")
                .build();
        try {
            userService.update(userDto, 20L);
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }
}
