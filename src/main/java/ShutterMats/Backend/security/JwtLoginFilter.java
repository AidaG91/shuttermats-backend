package ShutterMats.Backend.security;

import ShutterMats.Backend.dto.request.AdminLoginRequestDTO;
import ShutterMats.Backend.dto.response.AdminLoginResponseDTO;
import ShutterMats.Backend.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de login estilo clase (JWTAuthentication del ejemplo de
 * femcoders): intercepta el POST de login directamente, sin pasar por un
 * @RestController. La diferencia respecto al ejemplo: aqui devolvemos el
 * token en el body como JSON (no en la cabecera Authorization de la
 * respuesta), para mantener el mismo formato que el resto de la API y no
 * tener que reescribir el httpClient del frontend para leer cabeceras.
 */
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtLoginFilter(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            ObjectMapper objectMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            AdminLoginRequestDTO credentials =
                    objectMapper.readValue(request.getInputStream(), AdminLoginRequestDTO.class);

            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    credentials.username(), credentials.password());

            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido leer el cuerpo de la petición", e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        List<String> roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .toList();

        String token = jwtService.generateToken(authResult.getName(), roles);
        AdminLoginResponseDTO body = AdminLoginResponseDTO.of(token, jwtService.getExpirationMs());

        writeJson(response, HttpStatus.OK, body);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        ApiError body = ApiError.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Usuario o contraseña incorrectos"
        );

        writeJson(response, HttpStatus.UNAUTHORIZED, body);
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}
