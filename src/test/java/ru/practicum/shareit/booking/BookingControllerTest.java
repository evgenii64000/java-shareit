package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFront;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private BookingController bookingController;
    @Mock
    private BookingService bookingService;
    @Autowired
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;

    private BookingDtoFront bookingDtoFront = BookingDtoFront.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusSeconds(1L).withNano(0))
            .end(LocalDateTime.now().plusSeconds(2L).withNano(0))
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(bookingDtoFront.getStart())
            .end(bookingDtoFront.getEnd())
            .build();

    @BeforeEach
    void setUp() {
        bookingController = new BookingController(bookingService);
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void testPostBooking() throws Exception {
        Mockito.when(bookingService.createBooking(bookingDtoFront, 1L))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoFront))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testPatchOwnerBooking() throws Exception {
        when(bookingService.updateBookingByUser(1L, 1L, true))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetBooking() throws Exception {
        when(bookingService.getBooking(1L, 1L))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetBookingsForUser() throws Exception {
        when(bookingService.getBookingsForUser(1L, "ALL", 0, 20))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));
    }

    @Test
    public void testGetBookingsForOwner() throws Exception {
        when(bookingService.getBookingsForOwner(1L, "ALL", 0, 20))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));
    }
}
