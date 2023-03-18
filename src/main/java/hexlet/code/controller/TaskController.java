package hexlet.code.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.NoSuchElementException;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping ("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    private static final String ONLY_AUTHOR_BY_ID = """
        @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    @Operation (summary = "Get task by ID")
    @GetMapping (ID)
    public Task getTask(@PathVariable long id) throws NoSuchElementException {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Get tasks by filter")
    @ApiResponses (@ApiResponse (responseCode = "200", content =
        @Content (schema =
        @Schema (implementation = Task.class))
        ))
    @GetMapping("")
    public Iterable<Task> getFilteredTasks(
        @RequestParam (required = false) Map<String, String> requestParams
    ) throws JsonProcessingException {
        return requestParams.isEmpty() ? taskRepository.findAll() : taskService.getFilteredTasks(requestParams);
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping("")
    @ResponseStatus (CREATED)
    public Task createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update task")
    @PatchMapping (ID)
    public Task updateTask(@PathVariable long id, @RequestBody @Valid TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @Operation(summary = "Delete task")
    @DeleteMapping (ID)
    @PreAuthorize (ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable long id) {
        taskRepository.deleteById(id);
    }
}
