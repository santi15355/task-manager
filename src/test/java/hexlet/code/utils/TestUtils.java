package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Autowired
    private TaskStatusRepository taskStatusRepository;

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

    /**
     * Delete repositories.
     */
    public void tearDown() {
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    /**
     * Get User by email.
     *
     * @param email - name of a user
     * @return User object
     */
    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    /**
     * Create the default user.
     *
     * @return result of creating action
     */
    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    /**
     * Registering the user provided in the param.
     *
     * @param dto - name of a user
     * @return result data
     */
    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    /**
     * Create the token.
     *
     * @param userId - id of a user
     * @return token.
     */
    public String buildToken(Object userId) {
        return jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, userId));
    }

    /**
     * The base method of adding resource handlers.
     *
     * @param request - the request
     * @param byUser  - string data of user
     * @return system userDetails object
     */
    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final Long userId = userRepository.findByEmail(byUser)
                .map(User::getId)
                .orElse(null);

        final String token = buildToken(userId);
        return performWithToken(request, token);
    }

    /**
     * Performs the request.
     *
     * @param request - the request
     * @param token   - token
     * @return The result of an action using token
     */
    public ResultActions performWithToken(final MockHttpServletRequestBuilder request,
                                          final String token) throws Exception {
        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    /**
     * Peform the request without authentication.
     *
     * @param request - the request
     * @return The result of an action
     */
    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public TaskStatus postNewStatus(String txt, String userName) throws Exception {
        final var taskStatusDto = new TaskStatusDto(txt);

        final var response = perform(
                MockMvcRequestBuilders.post(TestUtils.TASK_STATUS_CONTROLLER_PATH)
                        .content(TestUtils.asJson(taskStatusDto))
                        .contentType(APPLICATION_JSON),
                userName
        ).andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        TaskStatus status = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        return status;
    }

    /**
     * Posts new task on behalfe of a user.
     * @param       txt - task Label
     * @param       userName - task Label
     * @return The result of an action
     */

    /**
     * Posts new task on behalfe of a user.
     * @param       txt - task Label
     * @param       userName - task Label
     * @return The result of an action
     */

    /**
     * Posts new task on behalfe of a user.
     * @param       queryStr - query string
     * @param       userName - task Label
     * @return The list of a task
     */

}
