package ShutterMats.Backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * UserDetails para el admin unico configurado por variables de entorno
 * (no hay tabla Admin en BD - ver KAN-78/79/80). Cuando exista Athlete
 * como entidad real, tendra su propio UserDetails respaldado por JPA.
 */
public class AdminUserDetails implements UserDetails {

    private final String username;
    private final String passwordHash;

    public AdminUserDetails(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
