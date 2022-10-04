package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

@SpringBootTest
public class UserMapperTest {

    private final User testUser = User.builder()
            .id(1L)
            .email("test@mail")
            .name("testName")
            .build();
    private final UserDto testUserDto = UserDto.builder()
            .id(1L)
            .email("test@mail")
            .name("testName")
            .build();

    @Test
    public void checkMapper() {
        UserDto userDto = UserMapper.toUserDto(testUser);
        User user = UserMapper.fromDtoToUser(testUserDto, 1L);
        Assertions.assertEquals(testUser, user);
        Assertions.assertEquals(testUserDto, userDto);
    }
}
