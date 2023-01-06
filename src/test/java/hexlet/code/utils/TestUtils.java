package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    public static final String USER_CONTROLLER_PATH = "/api/users";
    public static final String TASK_STATUS_CONTROLLER_PATH = "/api/statuses";
    public static final String TASK_CONTROLLER_PATH = "/api/tasks";
    public static final String LABEL_CONTROLLER_PATH = "/api/labels";
    public static final String LOGIN = "/api/login";
    public static final String ID = "/{id}";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "pwd"
    );
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    /**
     * Get the DTO for testing purposes.
     *
     * @return UserDto object
     */
    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public void tearDown() {
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public String buildToken(Object userId) {
        return jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, userId));
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final Long userId = userRepository.findByEmail(byUser)
                .map(User::getId)
                .orElse(null);

        final String token = buildToken(userId);
        return performWithToken(request, token);
    }

    public ResultActions performWithToken(final MockHttpServletRequestBuilder request,
                                          final String token) throws Exception {
        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

}
