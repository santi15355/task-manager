package hexlet.code.mapper;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaskMapper {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public Task createNewTask(final TaskDto taskDto) {
        return fromDto(taskDto);
    }

    public Task updateTask(final long id, final TaskDto taskDto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, taskDto);
        return task;
    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
    }

    private Task fromDto(final TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        final User executor = Optional.ofNullable(taskDto.getExecutorId())
                .map(User::new)
                .orElse(null);

        final TaskStatus taskStatus = Optional.ofNullable(taskDto.getTaskStatusId())
                .map(TaskStatus::new)
                .orElse(null);

        final Set<Label> labels = Optional.ofNullable(taskDto.getLabelIds())
                .orElse(Set.of())
                .stream()
                .filter(Objects::nonNull)
                .map(Label::new)
                .collect(Collectors.toSet());

        final Task task = new Task();
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setLabels(labels);
        task.setTaskStatus(taskStatus);
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());

        return task;
    }
}
