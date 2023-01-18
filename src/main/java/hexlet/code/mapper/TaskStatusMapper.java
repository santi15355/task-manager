package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskStatusMapper {
    public TaskStatus mapToTaskStatus(final TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        return mapToTaskStatus(taskStatus, taskStatusDto);
    }

    public TaskStatus mapToTaskStatus(final TaskStatus taskStatus, TaskStatusDto taskStatusDto) {
        taskStatus.setName(taskStatusDto.getName());
        return taskStatus;
    }
}
