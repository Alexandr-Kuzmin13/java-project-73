package hexlet.code.filter;

import hexlet.code.component.JWTHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import static hexlet.code.config.security.SecurityConfig.DEFAULT_AUTHORITIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    Logger logger = Logger.getLogger(JWTAuthorizationFilter.class.getName());

    private static final String BEARER = "Bearer";
    private final RequestMatcher publicUrls;
    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(final RequestMatcher fieldPublicUrls,
                                  final JWTHelper fieldJwtHelper) {
        this.publicUrls = fieldPublicUrls;
        this.jwtHelper = fieldJwtHelper;
    }

    @Override
    public boolean shouldNotFilter(final HttpServletRequest request) {
        return publicUrls.matches(request);
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain filterChain) throws ServletException, IOException {

        logger.info(SPRING_SECURITY_FORM_USERNAME_KEY);

        final var authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
            .map(header -> header.replaceFirst("^" + BEARER, ""))
            .map(String::trim)
            .map(jwtHelper::verify)
            .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
            .map(Object::toString)
            .map(this::buildAuthToken)
            .orElseThrow();


        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    public UsernamePasswordAuthenticationToken buildAuthToken(final String username) {
        return new UsernamePasswordAuthenticationToken(
            username,
            null,
            DEFAULT_AUTHORITIES
        );
    }
}
