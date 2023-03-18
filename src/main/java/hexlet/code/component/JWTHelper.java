package hexlet.code.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.impl.TextCodec.BASE64;

@Component
public class JWTHelper {



    private static final int TIME = 1000;
    private final String secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;
    private final Clock clock;

    public JWTHelper(@Value ("${jwt.issuer:spring_blog}") final String issuerField,
                     @Value("${jwt.expiration-sec:86400}") final Long expirationSecField,
                     @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSecField,
                     @Value("${jwt.secret:secret}") final String secret) {
        this.secretKey = BASE64.encode(secret);
        this.issuer = issuerField;
        this.expirationSec = expirationSecField;
        this.clockSkewSec = clockSkewSecField;
        this.clock = DefaultClock.INSTANCE;
    }

    public String expiring(final Map<String, String> attributes) {
        return Jwts.builder()
            .signWith(HS256, secretKey)
            .setClaims(getClaims(attributes, expirationSec))
            .compact();
    }

    public Map<String, Object> verify(final String token) {
        return Jwts.parser()
            .requireIssuer(issuer)
            .setClock(clock)
            .setAllowedClockSkewSeconds(clockSkewSec)
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
    }

    private Claims getClaims(final Map<String, String> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(clock.now());
        claims.putAll(attributes);
        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * TIME));
        }
        return claims;
    }
}
