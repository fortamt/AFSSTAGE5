package antifraud.controller;

import antifraud.model.User;
import antifraud.model.request.RoleRequest;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@Valid @RequestBody User user) {
        return userService.register(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));

    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    List<User> listUsers() {
        return userService.listUsers();
    }

    @DeleteMapping("/user/{username}")
    Map<String, String> delete(@PathVariable String username) {
        if (userService.delete(username)) {
            return Map.of(
                    "username", username,
                    "status", "Deleted successfully!"
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    User role(@RequestBody RoleRequest request) {
        return userService.changeRole(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/access")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> doLock(@RequestBody Map<String, String> lockUsers) {
        return userService.lock(lockUsers);
    }

}
