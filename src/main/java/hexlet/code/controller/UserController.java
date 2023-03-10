package hexlet.code.controller;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.validation.Valid;

import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getId().toString() == authentication.getName()
        """;

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Registration of a new user.
     * @param       dto user is being added
     * @return      new User
     */
    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid final UserDto dto) {
        return userService.createNewUser(dto);
    }

    /**
     * Get lis of users.
     * @return      List of Users
     */
    @Operation(summary = "Get list of Users")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = User.class))
    ))
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    /**
     * Get User by Id.
     * @param       id id of a User
     * @return      object User
     */
    @Operation(summary = "Get a User by Id")
    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public User getUserById(@PathVariable final Long id) {
        return userRepository.findById(id).get();
    }

    /**
     * Update of a user.
     * @param       id user id
     * @param       dto new user data
     * @return      new User
     */
    @PutMapping(ID)
    @Operation(summary = "Update a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User with that id not found")
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User update(@PathVariable final long id, @RequestBody @Valid final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    /**
     * Delete of a user.
     * @param       id user id
     */
    @Operation(summary = "Delete a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User with that id not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable final long id) {
        userRepository.deleteById(id);
    }


}

