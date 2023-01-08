package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createNewStatus(TaskStatusDto statusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(statusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateStatus(long id, TaskStatusDto statusDto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id).get();
        taskStatusToUpdate.setName(statusDto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }
}

