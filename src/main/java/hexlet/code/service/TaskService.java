package hexlet.code.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {

    Task findById(long id);

    List<Task> findAll();

    Task create(TaskDto dto);

    Task update(long id, TaskDto dto);

    void deleteById(long id);

    Iterable<Task> getFiltered(Map<String, String> requestParams) throws JsonProcessingException;
}
