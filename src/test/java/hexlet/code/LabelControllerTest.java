package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
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
public class LabelControllerTest {

    private final UserDto firstUserDto = UserControllerTest.getFirstUserDto();
    private final LabelDto firstLabel = new LabelDto("Story");
    private final LabelDto secondLabel = new LabelDto("Novel");
    private static String existingUserEmail;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @BeforeEach
    public void initialization() throws Exception {

        utils.setUp();
        utils.regEntity(firstUserDto, USER_CONTROLLER_PATH).andExpect(status().isCreated());
        existingUserEmail = userRepository.findAll().get(0).getEmail();
    }

    @Test
    public void registration() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH)
            .andExpect(status().isCreated());
        assertThat(labelRepository.count()).isEqualTo(1);
    }

    @Test
    public void testGetLabels() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH);
        utils.regEntity(secondLabel, existingUserEmail, LABEL_CONTROLLER_PATH);

        MockHttpServletResponse response = utils.perform(
                get(
                    BASE_URL + LABEL_CONTROLLER_PATH),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Story");
        assertThat(response.getContentAsString()).contains("Novel");
    }

    @Test
    public void testGetLabel() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH);
        Label expectedLabel = labelRepository.findAll().get(0);
        long expectedLabelId = expectedLabel.getId();

        MockHttpServletResponse response = utils.perform(
                get(BASE_URL + LABEL_CONTROLLER_PATH + ID, expectedLabelId),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(actualLabel.getName()).isEqualTo(expectedLabel.getName());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
    }
    @Test
    public void testUpDateLabel() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH);
        long labelToUpdateId = labelRepository.findAll().get(0).getId();

        MockHttpServletResponse response = utils.perform(
                patch(BASE_URL + LABEL_CONTROLLER_PATH + ID, labelToUpdateId)
                    .content(asJson(secondLabel))
                    .contentType(APPLICATION_JSON),
            existingUserEmail
        )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(labelRepository.findById(labelToUpdateId).get().getName())
            .isEqualTo(secondLabel.getName());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("Novel");
    }
    @Test
    public void testDeleteLabel() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH);
        long labelToDeleteId = labelRepository.findAll().get(0).getId();

        MockHttpServletResponse response = utils.perform(
            delete(BASE_URL + LABEL_CONTROLLER_PATH + ID, labelToDeleteId),
                existingUserEmail
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString()).doesNotContain("Story");
    }

    @Test
    public void deleteLabelWithTask() throws Exception {

        utils.regEntity(firstLabel, existingUserEmail, LABEL_CONTROLLER_PATH);
        long assignedLabelId = labelRepository.findAll().get(0).getId();
        utils.regEntity(new TaskStatusDto("Sample status"), existingUserEmail, STATUS_CONTROLLER_PATH);
        TaskDto taskDto = new TaskDto(
            "Task name",
            "Task description",
            taskStatusRepository.findAll().get(0).getId(),
            userRepository.findAll().get(0).getId(),
            Set.of(assignedLabelId)
        );
        utils.regEntity(taskDto, existingUserEmail, TASK_CONTROLLER_PATH);

        utils.perform(
            delete(BASE_URL + LABEL_CONTROLLER_PATH + ID, assignedLabelId),
                existingUserEmail
            )
            .andExpect(status().isUnprocessableEntity());
    }
}
