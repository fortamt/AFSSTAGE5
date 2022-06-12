package antifraud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "t_user")
public class User implements UserDetails, UserDetailsMixin {
    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @NotEmpty
    String name;
    @NotEmpty
    @Column(unique = true)
    String username;
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;
    @JsonIgnore
    boolean isAccountNonLocked;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }
}
