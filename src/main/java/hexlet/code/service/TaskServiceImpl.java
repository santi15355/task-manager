package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public Task createNewTask(final TaskDto dto) {
        final Task newTask = fromDto(dto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(final long id, final TaskDto dto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, dto);
        return taskRepository.save(task);
    }

    private void merge(final Task task, final TaskDto taskDto) {
        final Task newTask = fromDto(taskDto);
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setLabels(newTask.getLabels());
    }

    private Task fromDto(final TaskDto dto) {
        final User author = userService.getCurrentUser();
        final User executor = Optional.ofNullable(dto.getExecutorId())
                .map(User::new)
                .orElse(null);

        final TaskStatus taskStatus = Optional.ofNullable(dto.getTaskStatusId())
                .map(TaskStatus::new)
                .orElse(null);

        final Set<Label> labels = Optional.ofNullable(dto.getLabelIds())
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
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        return task;
    }
}
