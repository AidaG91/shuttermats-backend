package ShutterMats.Backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminUserDetailsServiceTest {

    private final AdminUserDetailsService service =
            new AdminUserDetailsService("admin", "hashed-password");

    @Test
    void loadUserByUsername_returnsAdminUser_whenUsernameMatches() {
        UserDetails user = service.loadUserByUsername("admin");

        assertEquals("admin", user.getUsername());
        assertEquals("hashed-password", user.getPassword());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_throws_whenUsernameDoesNotMatch() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("otro"));
    }
}
