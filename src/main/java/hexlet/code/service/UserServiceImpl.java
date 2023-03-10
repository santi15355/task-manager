package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hexlet.code.config.security.SecurityConfig.DEFAULT_AUTHORITIES;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public User createNewUser(final UserDto userDto) {
        return userRepository.save(userMapper.mapToUser(userDto));
    }

    @Override
    public User updateUser(final Long id, final UserDto userDto) {
        final User updatedUser = userRepository.findById(id).get();
        return userRepository.save(userMapper.mapToUser(updatedUser, userDto));
    }

    @Override
    public Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId()).get();
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::buildSpringUser)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user with 'username': " + username));
    }

    private UserDetails buildSpringUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPassword(),
                DEFAULT_AUTHORITIES
        );
    }
}
