package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.config.security.SecurityConfig.LOGIN;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith (SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerTest {

    public static final UserDto FIRST_USER_DTO = fromJson(
        TestUtils.readFixtureJson("first_user_dto.json"),
        new TypeReference<>() {
        }
    );
    public static final UserDto SECOND_USER_DTO = fromJson(
        TestUtils.readFixtureJson("second_user_dto.json"),
        new TypeReference<>() {
        }
    );

    private static User existingUser;

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
        utils.regEntity(FIRST_USER_DTO, USER_CONTROLLER_PATH)
            .andExpect(status().isCreated());
        existingUser = userRepository.findAll().get(0);
    }

    @Test
    public void registration() throws Exception {

        utils.regEntity(SECOND_USER_DTO, USER_CONTROLLER_PATH).andExpect(status().isCreated());
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    public void testRootPage() throws Exception {

        MockHttpServletResponse response = utils.perform(
                get("/welcome")
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentAsString()).contains("Welcome to Spring");
    }

    @Test
    public void testLogin() throws Exception {

        LoginDto loginDto = new LoginDto(FIRST_USER_DTO.getEmail(), FIRST_USER_DTO.getPassword());

        utils.perform(
                post(BASE_URL + LOGIN)
                    .contentType(APPLICATION_JSON)
                    .content(asJson(loginDto))
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
    }

    @Test
    public void testGetUsers() throws Exception {

        utils.regEntity(SECOND_USER_DTO, USER_CONTROLLER_PATH);

        MockHttpServletResponse response = utils
            .perform(
                get(BASE_URL + USER_CONTROLLER_PATH)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("london@email.com", "Jack", "London");
        assertThat(response.getContentAsString()).contains("twain@email.com", "Mark", "Twain");
        assertThat(users.get(0).getPassword()).isNull();
    }

    @Test
    public void testGetUser() throws Exception {

        MockHttpServletResponse response = utils
            .perform(
                get(BASE_URL + USER_CONTROLLER_PATH + ID, existingUser.getId()),
                existingUser.getEmail()
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        User user = fromJson(response.getContentAsString(), new TypeReference<>() { });

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("london@email.com", "Jack", "London");
        assertThat(user.getPassword()).isNull();
    }

    @Test
    public void testUpDateUser() throws Exception {

        utils.perform(
                put(BASE_URL + USER_CONTROLLER_PATH + ID, existingUser.getId())
                    .contentType(APPLICATION_JSON)
                    .content(asJson(SECOND_USER_DTO)),
                existingUser.getEmail()
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        User expectedAfterUser = userRepository.findAll().get(0);

        MockHttpServletResponse response = utils
            .perform(
                get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedAfterUser.getId()),
                expectedAfterUser.getEmail()
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(userRepository.findByEmail(existingUser.getEmail())).isEmpty();
        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains("twain@email.com", "Mark", "Twain");
    }

    @Test
    public void testDeleteUser() throws Exception {

        utils.perform(
                delete(BASE_URL + USER_CONTROLLER_PATH + ID, existingUser.getId()),
                existingUser.getEmail()
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        MockHttpServletResponse response = utils
            .perform(
                get(BASE_URL + USER_CONTROLLER_PATH)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        assertThat(response.getContentType()).isEqualTo(APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).doesNotContain("london@email.com", "Jack", "London");
    }

    @Test
    public void deleteUserWithTask() throws Exception {

        utils.regEntity(new TaskStatusDto("Status"), existingUser.getEmail(), STATUS_CONTROLLER_PATH);
        utils.regEntity(new LabelDto("Label"), existingUser.getEmail(), LABEL_CONTROLLER_PATH);

        TaskDto taskDto = new TaskDto(
            "Task name",
            "Task description",
            taskStatusRepository.findAll().get(0).getId(),
            userRepository.findAll().get(0).getId(),
            Set.of(labelRepository.findAll().get(0).getId())
        );
        utils.regEntity(taskDto, existingUser.getEmail(), TASK_CONTROLLER_PATH);

        utils.perform(
            delete(BASE_URL + USER_CONTROLLER_PATH + ID, existingUser.getId()),
                existingUser.getEmail()
            )
            .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void deleteUserByUser() throws Exception {

        utils.regEntity(SECOND_USER_DTO, USER_CONTROLLER_PATH);
        User userToDelete = userRepository.findAll().get(0);
        User actualUser = userRepository.findAll().get(1);

        utils.perform(
            delete(BASE_URL + USER_CONTROLLER_PATH + ID, userToDelete.getId()),
                actualUser.getEmail()
            )
            .andExpect(status().isForbidden());
    }
}
