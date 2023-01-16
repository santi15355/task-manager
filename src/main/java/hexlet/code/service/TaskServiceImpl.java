package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    @Override
    public Task createNewTask(final TaskDto taskDto) {
        return taskRepository.save(taskMapper.createNewTask(taskDto));
    }

    @Override
    public Task updateTask(final long id, final TaskDto taskDto) {
        return taskRepository.save(taskMapper.updateTask(id, taskDto));
    }
}
