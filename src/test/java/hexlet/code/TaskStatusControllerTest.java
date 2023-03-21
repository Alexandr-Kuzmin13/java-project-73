package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith (SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskStatusControllerTest {

    private final TaskStatusDto firstTaskStatus = new TaskStatusDto("Write");
    private final TaskStatusDto secondTaskStatus = new TaskStatusDto("Reader");
    private final UserDto firstUserDto = UserControllerTest.getFirstUserDto();
    private static String existingUserEmail;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void initialization() throws Exception {

        utils.setUp();
        utils.regEntity(firstUserDto, USER_CONTROLLER_PATH)
            .andExpect(status().isCreated());
        existingUserEmail = userRepository.findAll().get(0).getEmail();
    }

    @Test
    public void registration() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH)
            .andExpect(status().isCreated());
    }

    @Test
    public void testGetStatuses() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);
        utils.regEntity(secondTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);

        MockHttpServletResponse response = utils.perform(
                get(
                    BASE_URL + STATUS_CONTROLLER_PATH),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Write");
        assertThat(response.getContentAsString()).contains("Reader");
    }

    @Test
    public void testGetStatus() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);
        TaskStatus expectedStatus = taskStatusRepository.findAll().get(0);
        long expectedStatusId = expectedStatus.getId();

        MockHttpServletResponse response = utils.perform(
                get(
                    BASE_URL + STATUS_CONTROLLER_PATH + ID, expectedStatusId),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        TaskStatus actualStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(actualStatus.getName()).isEqualTo(expectedStatus.getName());
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
    }
    @Test
    public void testUpDateStatus() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);
        long statusToUpdateId = taskStatusRepository.findAll().get(0).getId();

        MockHttpServletResponse response = utils.perform(
                patch(BASE_URL + STATUS_CONTROLLER_PATH + ID, statusToUpdateId)
                    .content(asJson(secondTaskStatus))
                    .contentType(APPLICATION_JSON),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(taskStatusRepository.findById(statusToUpdateId).get().getName())
            .isEqualTo(secondTaskStatus.getName());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Reader");
    }
    @Test
    public void testDeleteStatus() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);
        long statusDeleteId = taskStatusRepository.findAll().get(0).getId();

        MockHttpServletResponse response = utils.perform(
            delete(BASE_URL + STATUS_CONTROLLER_PATH + ID, statusDeleteId),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString()).doesNotContain("Write");
    }

    @Test
    public void deleteStatusWithTask() throws Exception {

        utils.regEntity(firstTaskStatus, existingUserEmail, STATUS_CONTROLLER_PATH);
        long assignedStatusId = taskStatusRepository.findAll().get(0).getId();
        utils.regEntity(new LabelDto("Label"), existingUserEmail, LABEL_CONTROLLER_PATH);

        TaskDto taskDto = new TaskDto(
            "Task name",
            "Task description",
            assignedStatusId,
            userRepository.findAll().get(0).getId(),
            Set.of(labelRepository.findAll().get(0).getId())
        );

        utils.regEntity(taskDto, existingUserEmail, TASK_CONTROLLER_PATH);

        utils.perform(
            delete(BASE_URL + STATUS_CONTROLLER_PATH + ID, assignedStatusId),
                existingUserEmail
            )
            .andExpect(status().isUnprocessableEntity());

    }

}
