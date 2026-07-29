package ShutterMats.Backend.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthenticationManager implements AuthenticationManager {

    private final AdminUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationManager(
            AdminUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());

        if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
