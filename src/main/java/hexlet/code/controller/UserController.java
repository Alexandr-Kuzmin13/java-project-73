package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@AllArgsConstructor
@NoArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {

    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    @Autowired
    private Rollbar rollbar;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Operation(summary = "Get user by ID")
    @GetMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User getUser(@PathVariable long id) throws NoSuchElementException {

        return this.userRepository.findById(id).get();
    }

    @Operation(summary = "Get list of users")
    @ApiResponses (@ApiResponse(responseCode = "200", content =
        @Content (schema =
        @Schema (implementation = User.class))
        ))
    @GetMapping("")
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Operation (summary = "Create new user")
    @ApiResponse (responseCode = "201", description = "User created")
    @PostMapping("")
    @ResponseStatus (HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid UserDto dto) {
        return this.userService.createNewUser(dto);
    }

    @Operation(summary = "Update user")
    @PatchMapping (ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@RequestBody @Valid UserDto dto, @PathVariable long id) {
        return this.userService.updateUser(id, dto);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping(ID)
    @PreAuthorize (ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable long id) {
        rollbar.debug("Here is some debug message");
        this.userRepository.deleteById(id);
    }

}
