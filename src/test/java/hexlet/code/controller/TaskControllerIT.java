package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TASK_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void before() throws Exception {
        utils.regDefaultUser();
    }

    @AfterEach
    public void tearDown() {
        utils.tearDown();
    }

    private TaskDto buildTask() {
        return buildTask("test name");
    }

    private TaskDto buildTask(final String name) {
        return new TaskDto(
                name,
                "test description",
                null,
                null,
                Set.of()
        );
    }

    private TaskDto buildTask(final String name, final Long taskStatusId) {
        return new TaskDto(
                name,
                "test description",
                null,
                taskStatusId,
                Set.of()
        );
    }

    @Test
    public void getAll() throws Exception {

        final List<Task> expected = IntStream.range(1, 10)
                .mapToObj(i -> Task.builder()
                        .author(utils.getUserByEmail(TEST_USERNAME))
                        .description("description" + i)
                        .name("name" + i)
                        .build()
                ).toList();

        taskRepository.saveAll(expected);

        final var response = utils.perform(get(TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> posts = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(posts).hasSize(expected.size());
    }

    @Test
    public void getById() throws Exception {

        final Task expected = taskRepository.save(Task.builder()
                .author(utils.getUserByEmail(TEST_USERNAME))
                .description("description")
                .name("name")
                .build()
        );

        final var request = get(TASK_CONTROLLER_PATH + ID, expected.getId());

        final var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actual = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId());
    }


    @Test
    public void createTask() throws Exception {

        final User user = userRepository.findByEmail(TEST_USERNAME).get();

        final TaskStatus taskStatus = taskStatusRepository.save(TaskStatus.builder()
                .name("task status")
                .build()
        );

        final Label label1 = labelRepository.save(Label.builder().name("label1").build());
        final Label label2 = labelRepository.save(Label.builder().name("label2").build());

        final var task = new TaskDto(
                "test task",
                "test description",
                user.getId(),
                taskStatus.getId(),
                Set.of(label1.getId(), label2.getId())
        );

        createTask(task).andExpect(status().isCreated());

        assertFalse(taskRepository.findAll().isEmpty());
    }

    @Disabled("For now active only positive tests")
    @Test
    public void createTaskFails() throws Exception {

        final var task = buildTask("");

        createTask(task).andExpect(status().isUnprocessableEntity());

        assertTrue(taskRepository.findAll().isEmpty());
    }

    @Test
    public void updateTask() throws Exception {
        final User user = userRepository.findByEmail(TEST_USERNAME).get();

        final TaskStatus taskStatus = taskStatusRepository.save(TaskStatus.builder()
                .name("task status")
                .build()
        );

        final var task = new TaskDto(
                "test task",
                "test description",
                user.getId(),
                taskStatus.getId(),
                Set.of()
        );

        final var response = createTask(task)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task createdTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final var toUpdate = buildTask("new name", taskStatus.getId());

        final var request = put(TASK_CONTROLLER_PATH + ID, createdTask.getId())
                .content(asJson(toUpdate))
                .contentType(APPLICATION_JSON);

        utils.perform(request, TEST_USERNAME)
                .andExpect(status().isOk());
    }

    @Test
    public void getFiltered() throws Exception {
        final User user = userRepository.findByEmail(TEST_USERNAME).get();

        final TaskStatus taskStatus = taskStatusRepository.save(TaskStatus.builder()
                .name("task status")
                .build()
        );

        final var task = new TaskDto(
                "test name",
                "test description",
                user.getId(),
                taskStatus.getId(),
                Set.of()
        );

        createTask(task).andExpect(status().isCreated());

        final var response = utils.perform(
                        get(TASK_CONTROLLER_PATH).param("name", "test name"),
                        TEST_USERNAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Iterable<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(1);
    }

    @Test
    public void deleteTask() throws Exception {
        final Task task = taskRepository.save(Task.builder()
                .name("t name")
                .description("desc")
                .author(utils.getUserByEmail(TEST_USERNAME))
                .build());

        utils.perform(delete(TASK_CONTROLLER_PATH + ID, task.getId()), TEST_USERNAME)
                .andExpect(status().isOk());

        assertFalse(taskRepository.existsById(task.getId()));

    }

    @Disabled("For now active only positive tests")
    @Test
    public void deletePostFail() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME_2, "fname", "lname", "pwd"));

        final Task task = taskRepository.save(Task.builder()
                .name("t name")
                .description("desc")
                .author(utils.getUserByEmail(TEST_USERNAME_2))
                .build()
        );

        utils.perform(delete(TASK_CONTROLLER_PATH + ID, task.getId()), TEST_USERNAME)
                .andExpect(status().isForbidden());

        assertTrue(taskRepository.existsById(task.getId()));

    }

    private ResultActions createTask(final TaskDto task) throws Exception {
        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(task))
                .contentType(APPLICATION_JSON);

        return utils.perform(request, TEST_USERNAME);
    }

}
