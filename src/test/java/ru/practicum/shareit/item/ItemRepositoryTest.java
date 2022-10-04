package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User owner = User.builder()
            .name("owner")
            .email("owner@mail")
            .build();

    private User requestor = User.builder()
            .name("requestor")
            .email("requestor@mail")
            .build();

    private ItemRequest request = ItemRequest.builder()
            .description("some description")
            .build();

    private Item item1 = Item.builder()
            .name("testname")
            .description("this is description")
            .available(false)
            .build();

    private Item item2 = Item.builder()
            .name("anothername")
            .description("another description")
            .available(true)
            .build();

    private Item item3 = Item.builder()
            .name("TeSt")
            .description("just description")
            .available(true)
            .build();

    private void preparation() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();

        owner = userRepository.save(owner);
        requestor = userRepository.save(requestor);

        request.setRequestor(requestor);
        request = requestRepository.save(request);

        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2.setOwner(owner);
        item2.setRequest(request);
        item2 = itemRepository.save(item2);

        item3.setOwner(owner);
        item3.setRequest(request);
        item3 = itemRepository.save(item3);
    }

    @Test
    public void testFindByNameAndDescription() {
        preparation();
        List<Item> items = itemRepository.findByNameAndDescription("test", "test", PageRequest.of(0 / 20, 20))
                .stream().collect(Collectors.toList());
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item3, items.get(0));
    }

    @Test
    public void testGetAnswers() {
        preparation();
        List<Item> items = new ArrayList<>(itemRepository.getAnswers(request.getId()));
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(item2, items.get(0));
        Assertions.assertEquals(item3, items.get(1));
    }
}
