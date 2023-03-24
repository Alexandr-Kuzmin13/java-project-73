package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static hexlet.code.UserControllerTest.FIRST_USER_DTO;
import static hexlet.code.UserControllerTest.SECOND_USER_DTO;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith (SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerTest {

    private static String existingUserEmail;
    private static TaskDto firstTaskDto;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void initialization() throws Exception {
        utils.setUp();
        utils.regEntity(FIRST_USER_DTO, USER_CONTROLLER_PATH).andExpect(status().isCreated());
        existingUserEmail = userRepository.findAll().get(0).getEmail();
        long executorId = userRepository.findAll().get(0).getId();

        utils.regEntity(new TaskStatusDto("Write"), existingUserEmail, STATUS_CONTROLLER_PATH);
        utils.regEntity(new LabelDto("Story"), existingUserEmail, LABEL_CONTROLLER_PATH);
        firstTaskDto = new TaskDto(
            "Create a character",
            "With character",
            taskStatusRepository.findAll().get(0).getId(),
            executorId,
            Set.of(labelRepository.findAll().get(0).getId())
        );
    }

    @Test
    public void registration() throws Exception {

        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH)
            .andExpect(status().isCreated());
        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    public void testGetTasks() throws Exception {

        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);
        TaskDto secondTaskDto = new TaskDto();
        secondTaskDto.setName("Think over the plot");
        secondTaskDto.setTaskStatusId(firstTaskDto.getTaskStatusId());
        secondTaskDto.setExecutorId(firstTaskDto.getExecutorId());
        utils.regEntity(secondTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);

        MockHttpServletResponse response = utils.perform(
            get(BASE_URL + TASK_CONTROLLER_PATH),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString())
            .contains("Create a character", "With character", "Think over the plot");
    }

    @Test
    public void testGetTask() throws Exception {
        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);
        Task expectedTask = taskRepository.findAll().get(0);
        long authorId = expectedTask.getAuthor().getId();
        String authorEmail = userRepository.findById(authorId).get().getEmail();

        MockHttpServletResponse response = utils.perform(
                get(
                    BASE_URL + TASK_CONTROLLER_PATH + ID, expectedTask.getId()),
                authorEmail)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        Task task = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(task.getName()).isEqualTo(expectedTask.getName());
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Create a character", "With character");
    }

    @Test
    public void testUpDateTask() throws Exception {

        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);
        Task taskToUpdate = taskRepository.findAll().get(0);
        long taskToUpdateId = taskToUpdate.getId();
        String authorEmail = taskToUpdate.getAuthor().getEmail();
        TaskDto anotherTaskDto = new TaskDto();
        anotherTaskDto.setName("New task");
        anotherTaskDto.setTaskStatusId(firstTaskDto.getTaskStatusId());
        anotherTaskDto.setExecutorId(firstTaskDto.getExecutorId());

        MockHttpServletResponse response = utils.perform(
            put(BASE_URL + TASK_CONTROLLER_PATH + ID, taskToUpdateId)
            .content(asJson(anotherTaskDto))
            .contentType(APPLICATION_JSON),
                authorEmail
        )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(taskRepository.existsById(taskToUpdateId)).isTrue();
        assertThat(taskRepository.findByName(taskToUpdate.getName())).isEmpty();
        assertThat(taskRepository.findByName(anotherTaskDto.getName())).isPresent();
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("New task");
    }
    @Test
    public void testDeleteTask() throws Exception {

        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);
        Task existingTask = taskRepository.findAll().get(0);
        long existingTaskId = existingTask.getId();
        String authorEmail = existingTask.getAuthor().getEmail();

        MockHttpServletResponse response = utils.perform(
            delete(BASE_URL + TASK_CONTROLLER_PATH + ID, existingTaskId),
                authorEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString()).doesNotContain("Create a character", "With character");
    }

    @Test
    public void getFilteredTask() throws Exception {

        utils.regEntity(firstTaskDto, existingUserEmail, TASK_CONTROLLER_PATH);
        utils.regEntity(SECOND_USER_DTO, USER_CONTROLLER_PATH);
        User secondUser = userRepository.findAll().get(1);
        String secondUserEmail = secondUser.getEmail();
        utils.regEntity(new TaskStatusDto("New status"), secondUserEmail, STATUS_CONTROLLER_PATH);
        utils.regEntity(new LabelDto("New label"), secondUserEmail, LABEL_CONTROLLER_PATH);
        TaskDto secondTaskDto = new TaskDto(
            "New name",
            "New description",
            taskStatusRepository.findAll().get(1).getId(),
            secondUser.getId(),
            Set.of(labelRepository.findAll().get(1).getId())
        );
        utils.regEntity(secondTaskDto, secondUserEmail, TASK_CONTROLLER_PATH);

        long totalCount = taskRepository.count();
        long expectedCount = 1;

        Task taskToFind = taskRepository.findAll().get(0);
        long executorId = taskToFind.getExecutor().getId();
        long taskStatusId = taskToFind.getTaskStatus().getId();
        long labelId = taskToFind.getLabels().stream()
            .filter(label -> label.getName().equals("Story"))
            .findFirst().get()
            .getId();

        MockHttpServletResponse response = utils.perform(
                get(BASE_URL + TASK_CONTROLLER_PATH).param(
                "executorId", executorId + "",
                "taskStatus", taskStatusId + "",
                "labels", labelId + ""),
                existingUserEmail
            )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Task> filteredTasks = fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat((long) filteredTasks.size()).isNotEqualTo(totalCount);
        assertThat((long) filteredTasks.size()).isEqualTo(expectedCount);
    }
}
