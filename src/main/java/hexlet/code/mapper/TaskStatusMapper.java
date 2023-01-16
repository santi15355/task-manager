package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskStatusMapper {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatus createNewStatus(TaskStatusDto statusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(statusDto.getName());
        return taskStatus;
    }

    public TaskStatus updateStatus(long id, TaskStatusDto statusDto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id).get();
        taskStatusToUpdate.setName(statusDto.getName());
        return taskStatusToUpdate;
    }
}
