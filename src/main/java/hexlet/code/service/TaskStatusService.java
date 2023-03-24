package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;

import java.util.List;

public interface TaskStatusService {

    TaskStatus findById(long id);

    List<TaskStatus> findAll();

    TaskStatus create(TaskStatusDto dto);

    TaskStatus update(long id, TaskStatusDto dto);

    void deleteById(long id);
}
