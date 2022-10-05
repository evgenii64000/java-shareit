package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemRequestService requestService;
    private final EntityManager entityManager;
    private final UserService userService;

    private UserDto userDto = UserDto.builder()
            .name("test")
            .email("test@mail")
            .build();

    private UserDto anotherUser = UserDto.builder()
            .name("another")
            .email("another@mail")
            .build();

    private ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("testDescription")
            .build();

    @Test
    public void testCreateRequest() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest request = query
                .setParameter("id", requestDto.getId())
                .getSingleResult();

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    public void testCreateRequestWrongUser() {
        try {
            requestService.createRequest(200L, requestDto);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetRequests() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        List<ItemRequestDto> requests = new ArrayList<>(requestService.getRequests(userDto.getId()));
        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requests.get(0).getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    public void testGetRequestsWrongUser() {
        try {
            requestService.getRequests(200L);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetUsersRequests() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        anotherUser = userService.create(anotherUser);
        List<ItemRequestDto> requests = new ArrayList<>(requestService.getUsersRequests(anotherUser.getId(), 0, 5));
        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requests.get(0).getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    public void testGetUsersRequestsWrongUser() {
        try {
            requestService.getUsersRequests(200L, 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetRequest() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        ItemRequestDto requestGet = requestService.getRequest(userDto.getId(), requestDto.getId());
        assertThat(requestGet.getId(), notNullValue());
        assertThat(requestGet.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestGet.getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    public void testGetRequestWrongUser() {
        try {
            userDto = userService.create(userDto);
            requestDto = requestService.createRequest(userDto.getId(), requestDto);
            requestService.getRequest(200L, requestDto.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetRequestWrongRequest() {
        try {
            userDto = userService.create(userDto);
            requestDto = requestService.createRequest(userDto.getId(), requestDto);
            requestService.getRequest(userDto.getId(), 200L);
        } catch (Exception e) {
            Assertions.assertEquals("Запрос с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetAnswers() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        ItemRequestDto requestGet = requestService.getRequest(userDto.getId(), requestDto.getId());
        assertThat(requestGet.getId(), notNullValue());
        assertThat(requestGet.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requestGet.getCreated(), equalTo(requestDto.getCreated()));
        assertThat(requestGet.getItems(), equalTo(Collections.emptyList()));
    }
}
