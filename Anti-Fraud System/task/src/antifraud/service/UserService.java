package antifraud.service;

import antifraud.model.Role;
import antifraud.model.User;
import antifraud.model.request.RoleRequest;
import antifraud.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static antifraud.model.Role.*;

@Service
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new UsernameNotFoundException("User " + username + " not found"));
    }

    @Transactional
    public Optional<User> register(User user) {
        if (userRepository.count() == 0) {
            user.setRole(ADMINISTRATOR);
            user.setAccountNonLocked(true);
        } else {
            user.setRole(MERCHANT);
            user.setAccountNonLocked(false);
        }
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            return Optional.empty();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    public List<User> listUsers() {
        return userRepository.findAll(
                Sort.sort(User.class).by(User::getId).ascending()
        );
    }

    public Map<String, String> lock(Map<String, String> lockUsers) {
        String username = lockUsers.get("username");
        String operation = lockUsers.get("operation");

        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (operation.equals("LOCK")) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            return Map.of("status", "User " + user.getUsername() + " locked!");
        } else if (operation.equals("UNLOCK")) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            return Map.of("status", "User " + user.getUsername() + " unlocked!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public boolean delete(String username) {
        return userRepository.deleteByUsernameIgnoreCase(username) == 1;
    }

    @Transactional
    public Optional<User> changeRole(RoleRequest request) {
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(request.getUsername());
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        var user = optionalUser.get();
        var role = request.getRole();
        validateRole(user, role);
        user.setRole(role);
        return Optional.of(userRepository.save(user));
    }

    private void validateRole(User user, Role role) {
        if (!Objects.equals(role, SUPPORT) && !Objects.equals(role, MERCHANT)
                || Objects.equals(role, ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(user.getRole(), role)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
