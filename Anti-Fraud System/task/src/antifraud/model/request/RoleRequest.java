package antifraud.model.request;

import antifraud.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    private String username;
    private Role role;
}
