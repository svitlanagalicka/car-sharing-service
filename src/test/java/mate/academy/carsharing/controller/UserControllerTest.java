package mate.academy.carsharing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.carsharing.dto.UpdateUserProfileRequestDto;
import mate.academy.carsharing.dto.UpdateUserRoleRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.model.Role;
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
class UserControllerTest {
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
    @DisplayName("Update user role successfully when user exists")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/users/add-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRole_returnUpdatedUser_success() throws Exception {
        UpdateUserRoleRequestDto request = new UpdateUserRoleRequestDto(Role.RoleName.MANAGER);

        MvcResult result = mockMvc.perform(put("/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto expected = new UserResponseDto();
        expected.setId(2L);
        expected.setEmail("jane.doe@example.com");
        expected.setFirstName("Jane");
        expected.setLastName("Doe");
        expected.setRole("MANAGER");

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return 404 when updating role for non-existing user")
    @WithMockUser(username = "manager@example.com", roles = {"MANAGER"})
    void updateUserRole_userNotFound() throws Exception {
        UpdateUserRoleRequestDto request = new UpdateUserRoleRequestDto(Role.RoleName.MANAGER);

        mockMvc.perform(put("/users/999/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get logged-in user info successfully")
    @WithMockUser(username = "jane.doe@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:database/cars/users/add-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUserInfo_returnUserInfo() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto expected = new UserResponseDto();
        expected.setId(2L);
        expected.setEmail("jane.doe@example.com");
        expected.setFirstName("Jane");
        expected.setLastName("Doe");
        expected.setRole("CUSTOMER");

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return 404 when logged-in user not found")
    @WithMockUser(username = "unknown@example.com", roles = {"CUSTOMER"})
    void getUserInfo_userNotFound() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update logged-in user profile successfully")
    @WithMockUser(username = "jane.doe@example.com", roles = {"CUSTOMER"})
    @Sql(scripts = "classpath:database/cars/users/add-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/users/delete-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserProfile_updatedUser_success() throws Exception {
        UpdateUserProfileRequestDto request = new UpdateUserProfileRequestDto(
                "Janet",
                "Robson");

        MvcResult result = mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto expected = new UserResponseDto();
        expected.setId(2L);
        expected.setEmail("jane.doe@example.com");
        expected.setFirstName("Janet");
        expected.setLastName("Robson");
        expected.setRole("CUSTOMER");

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return 404 when updating profile of non-existing user")
    @WithMockUser(username = "unknown@example.com", roles = {"CUSTOMER"})
    void updateUserProfile_userNotFound() throws Exception {
        UpdateUserProfileRequestDto request = new UpdateUserProfileRequestDto("Jane", "Bobson");

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
