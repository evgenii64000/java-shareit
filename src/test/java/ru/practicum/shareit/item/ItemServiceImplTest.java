package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService requestService;
    private final EntityManager entityManager;
    private ItemDto itemDto = ItemDto.builder()
            .name("test")
            .description("testDescription")
            .available(true)
            .requestId(null)
            .build();
    private UserDto userDto = UserDto.builder()
            .name("test")
            .email("test@mail")
            .build();
    private ItemRequestDto requestDto = ItemRequestDto.builder()
            .description("testDescription")
            .build();

    @Test
    public void testCreate() {
        userDto = userService.create(userDto);
        itemDto = itemService.create(userDto.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void testCreateWrongUserId() {
        try {
            itemService.create(200L, itemDto);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testCreateWithRequest() {
        userDto = userService.create(userDto);
        requestDto = requestService.createRequest(userDto.getId(), requestDto);
        itemDto.setRequestId(requestDto.getId());
        itemService.create(userDto.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getRequest().getId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    public void testUpdate() {
        userDto = userService.create(userDto);
        itemDto = itemService.create(userDto.getId(), itemDto);
        itemDto.setName("testUpdate");
        itemDto.setDescription("testUpdateDescription");
        itemDto.setAvailable(false);
        itemService.update(userDto.getId(), itemDto.getId(), itemDto);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void testUpdateWrongUser() {
        try {
            itemService.update(200L, 1L, itemDto);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateWrongItem() {
        try {
            userService.create(userDto);
            itemDto = itemService.create(userDto.getId(), itemDto);
            itemService.update(userDto.getId(), 200L, itemDto);
        } catch (Exception e) {
            Assertions.assertEquals("Предмет с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateWrongOwner() {
        try {
            userDto = userService.create(userDto);
            itemDto = itemService.create(userDto.getId(), itemDto);
            userDto.setName("another");
            userDto.setEmail("another@mail");
            userDto.setId(null);
            userDto = userService.create(userDto);
            itemService.update(userDto.getId(), itemDto.getId(), itemDto);
        } catch (Exception e) {
            Assertions.assertEquals("Неверный id пользователя", e.getMessage());
        }
    }

    @Test
    public void testGetItemById() {
        userDto = userService.create(userDto);
        itemDto = itemService.create(userDto.getId(), itemDto);
        ItemDtoWithBooking itemDtoBook = itemService.getItemById(itemDto.getId(), userDto.getId());
        assertThat(itemDtoBook.getId(), notNullValue());
        assertThat(itemDtoBook.getName(), equalTo(itemDto.getName()));
        assertThat(itemDtoBook.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemDtoBook.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void testGetByIdWrongUser() {
        try {
           itemService.getItemById(200L, 200L);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetByIdWrongItem() {
        try {
            userDto = userService.create(userDto);
            itemService.getItemById(200L, userDto.getId());
        } catch (Exception e) {
            Assertions.assertEquals("Предмет с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetUserItems() {
        userDto = userService.create(userDto);
        ItemDto itemDto1 = itemService.create(userDto.getId(), itemDto);
        itemDto.setName("testUpdate");
        itemDto.setDescription("testUpdateDescription");
        ItemDto itemDto2 = itemService.create(userDto.getId(), itemDto);
        List<ItemDtoWithBooking> items = new ArrayList<>(itemService.getUserItems(userDto.getId(), 0, 5));
        assertThat(items.get(0).getId(), equalTo(itemDto1.getId()));
        assertThat(items.get(0).getName(), equalTo(itemDto1.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto1.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(itemDto1.getAvailable()));
        assertThat(items.get(1).getId(), equalTo(itemDto2.getId()));
        assertThat(items.get(1).getName(), equalTo(itemDto2.getName()));
        assertThat(items.get(1).getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(items.get(1).getAvailable(), equalTo(itemDto2.getAvailable()));

    }

    @Test
    public void testGetUserItemsWrongUser() {
        try {
            itemService.getUserItems(200L, 10, 10);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testGetUserItemsWrongParam() {
        try {
            userDto = userService.create(userDto);
            itemService.getUserItems(userDto.getId(), -10, -10);
        } catch (Exception e) {
            Assertions.assertEquals("Неправильные параметры запроса", e.getMessage());
        }
    }

    @Test
    public void testFindItems() {
        userDto = userService.create(userDto);
        itemDto = itemService.create(userDto.getId(), itemDto);
        List<ItemDto> items = new ArrayList<>(itemService.findItems("test", 0, 5));
        assertThat(items.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(items.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(items.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void testFindItemsParam() {
        try {
            itemService.findItems("test", 0, 5);
        } catch (Exception e) {
            Assertions.assertEquals("Неправильные параметры запроса", e.getMessage());
        }
    }

    @Test
    public void testFindItemsBlank() {
        List<ItemDto> items = new ArrayList<>(itemService.findItems("", 0, 5));
        Assertions.assertEquals(Collections.emptyList(), items);
    }

    @Test
    public void testAddComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("this is comment)")
                .build();
        userDto.setName("owner");
        userDto.setEmail("owner@mail");
        UserDto owner = userService.create(userDto);
        userDto.setName("booker");
        userDto.setEmail("booker@mail");
        UserDto booker = userService.create(userDto);
        itemDto = itemService.create(owner.getId(), itemDto);
        BookingDtoFront booking = BookingDtoFront.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusNanos(1000000L))
                .end(LocalDateTime.now().plusSeconds(1L))
                .build();
        bookingService.createBooking(booking, booker.getId());
        CommentDto commentReturn = itemService.addComment(booker.getId(), itemDto.getId(), commentDto);
        assertThat(commentReturn.getId(), notNullValue());
        assertThat(commentReturn.getText(), equalTo(commentDto.getText()));
    }

    @Test
    public void testAddCommentWrongUser() {
        try {
            CommentDto commentDto = CommentDto.builder()
                    .text("this is comment)")
                    .build();
            itemService.addComment(200L, 1L, commentDto);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testAddCommentWrongItem() {
        try {
            CommentDto commentDto = CommentDto.builder()
                    .text("this is comment)")
                    .build();
            UserDto booker = userService.create(userDto);
            itemService.addComment(booker.getId(), 200L, commentDto);
        } catch (Exception e) {
            Assertions.assertEquals("Предмет с таким id не найден", e.getMessage());
        }
    }

    @Test
    public void testAddCommentWrongBooker() {
        try {
            CommentDto commentDto = CommentDto.builder()
                    .text("this is comment)")
                    .build();
            UserDto owner = userService.create(userDto);
            itemDto = itemService.create(owner.getId(), itemDto);
            itemService.addComment(owner.getId(), itemDto.getId(), commentDto);
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь не бронировал эту вещь", e.getMessage());
        }
    }

    @Test
    public void testAddCommentWrongTime() {
        try {
            CommentDto commentDto = CommentDto.builder()
                    .text("this is comment)")
                    .build();
            userDto.setName("owner");
            userDto.setEmail("owner@mail");
            UserDto owner = userService.create(userDto);
            userDto.setName("booker");
            userDto.setEmail("booker@mail");
            UserDto booker = userService.create(userDto);
            itemDto = itemService.create(owner.getId(), itemDto);
            BookingDtoFront booking = BookingDtoFront.builder()
                    .itemId(itemDto.getId())
                    .start(LocalDateTime.now().plusSeconds(1L))
                    .end(LocalDateTime.now().plusSeconds(1L))
                    .build();
            bookingService.createBooking(booking, booker.getId());
            CommentDto commentReturn = itemService.addComment(booker.getId(), itemDto.getId(), commentDto);
            assertThat(commentReturn.getId(), notNullValue());
            assertThat(commentReturn.getText(), equalTo(commentDto.getText()));
        } catch (Exception e) {
            Assertions.assertEquals("Пользователь ещё не пользовался этой вещью", e.getMessage());
        }
    }
}
