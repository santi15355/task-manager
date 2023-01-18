package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    private final TaskMapper taskMapper;

    @Override
    public Task createNewTask(final TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        Task task = taskMapper.mapToTask(taskDto);
        task.setAuthor(author);
        return taskRepository.save(task);
    }
    @Override
    public Task updateTask(final long id, final TaskDto taskDto) {
        final User author = userService.getCurrentUser();
        final Task taskToUpdate = taskRepository.findById(id).get();
        taskToUpdate.setAuthor(author);
        return taskRepository.save(taskMapper.mapToTask(taskToUpdate, taskDto));
    }
}
