package ShutterMats.Backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final String adminUsername;
    private final String adminPasswordHash;

    public AdminUserDetailsService(
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password-hash}") String adminPasswordHash
    ) {
        this.adminUsername = adminUsername;
        this.adminPasswordHash = adminPasswordHash;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!adminUsername.equals(username)) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return new AdminUserDetails(adminUsername, adminPasswordHash);
    }
}
