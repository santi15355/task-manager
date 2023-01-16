package hexlet.code.mapper;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public User createUser(final UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return user;
    }

    public User updateUser(final Long id, final UserDto userDto) {
        final User updatedUser = userRepository.findById(id).get();
        updatedUser.setEmail(userDto.getEmail());
        updatedUser.setFirstName(userDto.getFirstName());
        updatedUser.setLastName(userDto.getLastName());
        updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return updatedUser;
    }
}
