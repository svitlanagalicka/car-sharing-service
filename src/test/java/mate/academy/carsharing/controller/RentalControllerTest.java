package mate.academy.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import mate.academy.carsharing.dto.RentalRequestDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Rental creation is successful")
    @Sql(scripts = {
            "classpath:database/cars/rentals/delete-rentals.sql",
            "classpath:database/cars/users/add-users.sql",
            "classpath:database/cars/add-cars.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cars/rentals/delete-rentals.sql",
            "classpath:database/cars/delete-cars.sql",
            "classpath:database/cars/rentals/delete-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    void createRental_returnCreated_success() throws Exception {
        LocalDate returnDate = LocalDate.now().plusDays(5);
        RentalRequestDto requestDto = new RentalRequestDto(1L, returnDate);

        MvcResult result = mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class);

        RentalResponseDto expected = new RentalResponseDto(
                actual.id(),
                LocalDate.now(),
                returnDate,
                null,
                1L,
                1L);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Unable to create rental")
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    void createRental_invalidRequestDto_returnBadRequest() throws Exception {
        RentalRequestDto rentalRequestDto = new RentalRequestDto(null, null);
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);
        mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("Successfully finds rentals by ID")
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = {"classpath:database/cars/add-cars.sql",
            "classpath:database/cars/rentals/add-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/rentals/delete-rentals.sql",
            "classpath:database/cars/delete-cars.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentalById_returnRentalId_success() throws Exception {
        Long rentalId = 1L;
        RentalResponseDto expected = new RentalResponseDto(rentalId, LocalDate.of(2025, 9, 5),
                LocalDate.of(2025, 9, 10), null, 1L, 1L);
        MvcResult result = mockMvc.perform(get("/rentals/{id}", rentalId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                RentalResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    @DisplayName("Does not find rentals with an ID that does not exist")
    void getRentalById_returnNotFound_rentalNotExist() throws Exception {
        Long id = 999L;
        mockMvc.perform(get("/rentals/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Successfully returns the list of rentals")
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = {"classpath:database/cars/users/add-users.sql",
            "classpath:database/cars/add-cars.sql",
            "classpath:database/cars/rentals/add-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/rentals/delete-rentals.sql",
            "classpath:database/cars/delete-cars.sql",
            "classpath:database/cars/users/delete-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentals_returnListOfRentals() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("userId", "1")
                        .param("isActive", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<RentalResponseDto> actual = objectMapper.convertValue(
                root.get("content"),
                new TypeReference<List<RentalResponseDto>>() {});

        RentalResponseDto expectedRental = new RentalResponseDto(
                1L,
                LocalDate.of(2025, 9, 5),
                LocalDate.of(2025, 9, 10),
                null,
                1L,
                1L);
        List<RentalResponseDto> expected = List.of(expectedRental);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Returns an empty list of rentals")
    @WithMockUser(username = "non.existent@example.com", roles = {"CUSTOMER"})
    void getRentals_userNotExist_returnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/rentals")
                        .param("userId", "999")
                        .param("isActive", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");

        List<RentalResponseDto> rentals = objectMapper.convertValue(
                content,
                new TypeReference<List<RentalResponseDto>>() {});

        assertEquals(0, rentals.size());
    }

    @Test
    @DisplayName("Return rental successfully when rental exists")
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = {"classpath:database/cars/add-cars.sql",
            "classpath:database/cars/rentals/add-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/rentals/delete-rentals.sql",
            "classpath:database/cars/delete-cars.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void returnRental_success() throws Exception {
        Long rentalId = 1L;

        MvcResult result = mockMvc.perform(post("/rentals/" + rentalId + "/return")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RentalResponseDto.class);

        RentalResponseDto expected = new RentalResponseDto(
                1L,
                LocalDate.of(2025, 9, 5),
                LocalDate.of(2025, 9, 10),
                LocalDate.now(),
                1L,
                1L);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return rental fails with 404 when rental does not exist")
    @WithMockUser(username = "john.doe@example.com", roles = {"CUSTOMER"})
    void returnRental_rentalNotFound_throwsNotFound() throws Exception {
        Long rentalId = 999L;

        mockMvc.perform(post("/rentals/" + rentalId + "/return")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
