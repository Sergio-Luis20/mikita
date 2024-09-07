package mikita.user;

import lombok.AllArgsConstructor;
import mikita.exception.InvalidPasswordException;
import mikita.exception.InvalidUserException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    public static final Pattern USERNAME_PATTERN = Pattern.compile("\\A[A-Za-z0-9_-]{3,36}\\z");

    private UserRepository repository;
    private PasswordEncoder encoder;

    public MikitaUser createUser(String username, String rawPassword) {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(rawPassword, "rawPassword");
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new InvalidUserException("Invalid username: " + username + ". Must be " + USERNAME_PATTERN.pattern());
        }
        if (rawPassword.isBlank()) {
            throw new InvalidPasswordException("Invalid password: blank (or empty)");
        }
        String encodedPassword = encoder.encode(rawPassword);
        return new MikitaUser(username, encodedPassword);
    }

    @Override
    public MikitaUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findById(username).orElse(null);
    }

    public boolean exists(String username) {
        return repository.existsById(username);
    }

    public MikitaUser saveUser(MikitaUser user) {
        Objects.requireNonNull(user, "user");
        if (repository.existsById(user.getUsername())) {
            throw new InvalidUserException("User already exists in database");
        }
        return repository.save(user);
    }

    public MikitaUser updateUser(MikitaUser user) {
        Objects.requireNonNull(user, "user");
        return repository.save(user);
    }

    public MikitaUser removeUser(MikitaUser user) {
        if (user == null) {
            return null;
        }
        repository.deleteById(user.getUsername());
        return user;
    }

}
