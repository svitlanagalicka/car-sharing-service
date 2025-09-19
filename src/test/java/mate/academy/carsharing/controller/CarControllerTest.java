package mate.academy.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.util.TestUtil;
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
class CarControllerTest {

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
    @DisplayName("Successfully returns the list of cars")
    @WithMockUser(roles = "CUSTOMER")
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_returnListOfCars() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andReturn();

        CarResponseDto expectedCar = new CarResponseDto();
        expectedCar.setId(1L);
        expectedCar.setModel("KIA");
        expectedCar.setBrand("SOUL");
        expectedCar.setType(Car.CarType.HATCHBACK);
        expectedCar.setInventory(7);
        expectedCar.setDailyFee(new BigDecimal("199.00"));

        List<CarResponseDto> actual = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        new TypeReference<List<CarResponseDto>>() {});

        List<CarResponseDto> expected = List.of(expectedCar);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Does not return a list of cars without registration")
    void findAll_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Successfully finds cars by ID")
    @WithMockUser(roles = "CUSTOMER")
    @Sql(scripts = "classpath:database/cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getCarById_returnCar_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CarResponseDto expected = new CarResponseDto();
        expected.setId(1L);
        expected.setModel("KIA");
        expected.setBrand("SOUL");
        expected.setType(Car.CarType.valueOf("HATCHBACK"));
        expected.setInventory(7);
        expected.setDailyFee(new BigDecimal("199.00"));

        CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Does not find cars with an ID that does not exist")
    @WithMockUser(roles = "CUSTOMER")
    void getCarById_returnNotFound_carNotExist() throws Exception {
        Long id = 999L;
        mockMvc.perform(get("/cars/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("When the car was successfully saved")
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void save_validRequestDto_returnSuccess() throws Exception {
        CarRequestDto carRequestDto = TestUtil.carRequestDto();
        CarResponseDto expected = TestUtil.carResponseDto(1L);

        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);
        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CarResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Creating a car with incorrect data")
    void save_invalidRequestDto_returnBadRequest() throws Exception {
        CarRequestDto carRequestDto = new CarRequestDto();
        carRequestDto.setDailyFee(BigDecimal.valueOf(0));
        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);
        mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Return 200 OK when car is updated successfully by MANAGER")
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCar_returnUpdateCar_success() throws Exception {
        CarRequestDto carRequestDto = new CarRequestDto();
        carRequestDto.setModel("Updated Model");
        carRequestDto.setBrand("Updated Brand");
        carRequestDto.setType(Car.CarType.SEDAN);
        carRequestDto.setInventory(10);
        carRequestDto.setDailyFee(BigDecimal.valueOf(999));
        Long id = 1L;

        CarResponseDto expected = new CarResponseDto();
        expected.setId(id);
        expected.setModel("Updated Model");
        expected.setBrand("Updated Brand");
        expected.setType(Car.CarType.SEDAN);
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(999));

        MvcResult result = mockMvc.perform(put("/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        CarResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Return NOT_FOUND when car does not exist")
    void updateCar_returnNotFound_carNotExist() throws Exception {
        CarRequestDto requestDto = new CarRequestDto();
        requestDto.setModel("Model");
        requestDto.setBrand("Brand");
        requestDto.setType(Car.CarType.SEDAN);
        requestDto.setInventory(7);
        requestDto.setDailyFee(BigDecimal.valueOf(99));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Car successfully deleted by manager")
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_returnNoContent_success() throws Exception {
        mockMvc.perform(delete("/cars/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Return NOT_FOUND when car does not exist")
    void delete_returnNotFound_carNotExist() throws Exception {
        mockMvc.perform(delete("/cars/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Car search returns a list of cars based on the specified parameters")
    @Sql(scripts = "classpath:database/cars/add-cars.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchCars_returnSearchCars_success() throws Exception {
        MvcResult result = mockMvc.perform(get("/cars/search")
                .param("model", "KIA")
                .param("brand", "SOUL")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<CarResponseDto> actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(),
                new TypeReference<List<CarResponseDto>>() {});
        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        CarResponseDto firstCar = actual.get(0);
        assertEquals("KIA", firstCar.getModel());
        assertEquals("SOUL", firstCar.getBrand());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Car search returns an empty list if no cars are found")
    void searchCars_returnEmptyList_whenCarsNotFound() throws Exception {
        mockMvc.perform(get("/cars/search")
                        .param("model", "Unknown")
                        .param("brand", "Unknown"))
                .andExpect(status().isOk());
    }
}
