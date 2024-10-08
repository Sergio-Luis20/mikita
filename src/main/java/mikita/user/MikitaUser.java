package mikita.user;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class MikitaUser implements UserDetails, Serializable {

    @Id
    @Column(length = 36)
    private String username;

    @Getter
    private String email;

    @Column(length = 128)
    private String password; // This should be the encoded password

    @Enumerated
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<MikitaRole> roles;

    public MikitaUser() {
        this.roles = new HashSet<>();
    }

    public MikitaUser(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Set<MikitaRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean hasRole(MikitaRole role) {
        if (role == null) {
            return false;
        }
        return roles.contains(role);
    }

    public void addRole(MikitaRole role) {
        roles.add(Objects.requireNonNull(role, "role"));
    }

    public void removeRole(MikitaRole role) {
        if (role == null) {
            return;
        }
        roles.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
