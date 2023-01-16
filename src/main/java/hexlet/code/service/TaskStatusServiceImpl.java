package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusMapper taskStatusMapper;

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createNewStatus(TaskStatusDto statusDto) {
        return taskStatusRepository.save(taskStatusMapper.createNewStatus(statusDto));
    }

    @Override
    public TaskStatus updateStatus(long id, TaskStatusDto statusDto) {
        return taskStatusRepository.save(taskStatusMapper.updateStatus(id, statusDto));
    }
}

