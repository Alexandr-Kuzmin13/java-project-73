package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping ("${base-url}" + STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String STATUS_CONTROLLER_PATH = "/statuses";

    private TaskStatusService taskStatusService;

    @Operation (summary = "Get status by ID")
    @GetMapping (ID)
    public TaskStatus getStatus(@PathVariable long id) throws NoSuchElementException {
        return taskStatusService.findById(id);
    }

    @Operation(summary = "Get list of task statuses")
    @ApiResponses (@ApiResponse (responseCode = "200", content =
        @Content (schema =
        @Schema (implementation = TaskStatus.class))
        ))
    @GetMapping("")
    public List<TaskStatus> getStatuses() {
        return taskStatusService.findAll();
    }

    @Operation(summary = "Create new task status")
    @ApiResponse(responseCode = "201", description = "Task status created")
    @PostMapping ("")
    @ResponseStatus (CREATED)
    public TaskStatus createStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.create(taskStatusDto);
    }

    @Operation(summary = "Update task status")
    @PutMapping (ID)
    public TaskStatus updateStatus(@PathVariable long id, @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.update(id, taskStatusDto);
    }

    @Operation(summary = "Delete task status")
    @DeleteMapping (ID)
    public void deleteStatus(@PathVariable long id) {
        taskStatusService.deleteById(id);
    }

}
