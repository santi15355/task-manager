package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(TEST_PROFILE)
//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIT {

    private final String url = "/api/users/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearAll() {
        userRepository.deleteAll();
    }

    @Test
    public void createUserTest() throws Exception {
        UserDto userDto = objectMapper.readValue(resourceLoader
                .getResource("classpath:json/test_user.json").getFile(), UserDto.class);
        mockMvc.perform(post(url)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is(201));

        MockHttpServletResponse response = mockMvc
                .perform(get(url))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Elbert", "Abshire");
    }

    @Test
    public void updateUserTest() throws Exception {
        UserDto userFromBD = objectMapper.readValue(resourceLoader
                .getResource("classpath:json/test_user.json").getFile(), UserDto.class);
        mockMvc.perform(post(url)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFromBD)))
                .andExpect(status().is(201));

        UserDto updatedUser = objectMapper.readValue(resourceLoader
                .getResource("classpath:json/test_user_update.json").getFile(), UserDto.class);
        Long userId = userRepository.findByEmail("elbert_abshire52@gmail.com").get().getId();
        mockMvc.perform(put(url + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk());

        MockHttpServletResponse response = mockMvc
                .perform(get(url + userId))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Chpok");

    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto userDto = objectMapper.readValue(resourceLoader
                .getResource("classpath:json/test_user.json").getFile(), UserDto.class);
        mockMvc.perform(post(url)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is(201));

        User expectedUser = userRepository.findAll().get(0);

        MockHttpServletResponse response = mockMvc
                .perform(get(url + expectedUser.getId()))
                .andReturn()
                .getResponse();

        User userExpectedDto = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedUser.getEmail(), userExpectedDto.getEmail());
        assertEquals(expectedUser.getId(), userExpectedDto.getId());
        assertEquals(expectedUser.getLastName(), userExpectedDto.getLastName());
        assertEquals(expectedUser.getFirstName(), userExpectedDto.getFirstName());
    }

    @Test
    public void testDeleteUser() throws Exception {
        UserDto userDto = objectMapper.readValue(resourceLoader
                .getResource("classpath:json/test_user.json").getFile(), UserDto.class);

        mockMvc.perform(post(url)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is(201));

        Long userId = userRepository.findByEmail("elbert_abshire52@gmail.com").get().getId();
        mockMvc.perform(delete(url + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response = mockMvc
                .perform(get(url))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).doesNotContain("Elbert", "Abshire");
    }
}
