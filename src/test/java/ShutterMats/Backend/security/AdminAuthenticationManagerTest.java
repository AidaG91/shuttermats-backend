package ShutterMats.Backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuthenticationManagerTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD_HASH = "hashed-password";

    @Mock
    private AdminUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminAuthenticationManager authenticationManager;

    @BeforeEach
    void setup() {
        authenticationManager = new AdminAuthenticationManager(userDetailsService, passwordEncoder);
    }

    @Test
    void authenticate_returnsAuthenticationWithRole_whenCredentialsAreValid() {
        AdminUserDetails adminUser = new AdminUserDetails(USERNAME, PASSWORD_HASH);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(adminUser);
        when(passwordEncoder.matches("correct-password", PASSWORD_HASH)).thenReturn(true);

        Authentication request = new UsernamePasswordAuthenticationToken(USERNAME, "correct-password");
        Authentication result = authenticationManager.authenticate(request);

        assertEquals(USERNAME, result.getName());
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void authenticate_throwsBadCredentials_whenPasswordIsWrong() {
        AdminUserDetails adminUser = new AdminUserDetails(USERNAME, PASSWORD_HASH);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(adminUser);
        when(passwordEncoder.matches("wrong-password", PASSWORD_HASH)).thenReturn(false);

        Authentication request = new UsernamePasswordAuthenticationToken(USERNAME, "wrong-password");

        assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(request));
    }

    @Test
    void authenticate_propagatesUsernameNotFound_whenUserDoesNotExist() {
        when(userDetailsService.loadUserByUsername("desconocido"))
                .thenThrow(new UsernameNotFoundException("no existe"));

        Authentication request = new UsernamePasswordAuthenticationToken("desconocido", "cualquiera");

        assertThrows(UsernameNotFoundException.class, () -> authenticationManager.authenticate(request));
    }
}
