package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;


public interface TaskStatusService {
    TaskStatus createNewStatus(TaskStatusDto statusDto);

    TaskStatus updateStatus(long id, TaskStatusDto statusDto);
}
