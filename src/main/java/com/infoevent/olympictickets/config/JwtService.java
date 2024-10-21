package com.infoevent.olympictickets.config;


import com.infoevent.olympictickets.entity.Jwt;
import com.infoevent.olympictickets.entity.RefreshToken;
import com.infoevent.olympictickets.entity.User;
import com.infoevent.olympictickets.repository.JwtRepository;
import com.infoevent.olympictickets.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Transactional
//@NoArgsConstructor
@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public static final String BEARER = "bearer";
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INVALIDE = "Token invalide";



    private final UserService userService;
    private final JwtRepository jwtRepository;

    public JwtService(UserService userService, JwtRepository jwtRepository) {
        this.userService = userService;
        this.jwtRepository = jwtRepository;
    }

    public Jwt tokenByValue(String value) {
        return this.jwtRepository.findByValeurAndDesactiveAndExpire(
                value,
                false,
                false
        ).orElseThrow(() -> new RuntimeException("Token invalide ou inconnu"));
    }
    public Map<String, String> generate(String username) {
        User user = this.userService.loadUserByUsername(username);
        this.disableTokens(user);
        final Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(user));

        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30 * 60 * 1000))
                .build();

        final Jwt jwt = Jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .user(user)
                .refreshToken(refreshToken)
                .build();

        this.jwtRepository.save(jwt);
        jwtMap.put(REFRESH,  refreshToken.getValeur());
        return jwtMap;
    }

    private void disableTokens(User user) {
        final List<Jwt> jwtList = this.jwtRepository.findUser(user.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
    }

    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(User user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;

        final Map<String, Object> claims = Map.of(
                "firstName", user.getFirstName(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, user.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(user.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER, bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void deconnexion() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findUserValidToken(
                user.getEmail(),
                false,
                false
        ).orElseThrow(() -> new RuntimeException(TOKEN_INVALIDE));
        jwt.setExpire(true);
        jwt.setDesactive(true);
        this.jwtRepository.save(jwt);
    }

    @Scheduled(cron = "0 0 */10 * * *")
    //@Scheduled(cron = "@daily")
    public void removeUselessJwt() {
        log.info("Suppression des token à {}", Instant.now());
        this.jwtRepository.deleteAll();
    }



    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
        final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new RuntimeException(TOKEN_INVALIDE));
        if(jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException(TOKEN_INVALIDE);
        }
        this.disableTokens(jwt.getUser());
        return this.generate(jwt.getUser().getEmail());
    }

}