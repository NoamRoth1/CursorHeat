package org.cursorheat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service responsible for JWT (JSON Web Token) operations including token generation,
 * validation, and claim extraction.
 * 
 * This service handles all JWT-related operations for the application's authentication system.
 * It provides methods for generating tokens, validating tokens, and extracting claims from tokens.
 * The service uses HS256 algorithm for token signing and includes standard JWT claims
 * such as subject (username), issued at, and expiration time.
 *
 * @author CursorHeat Team
 * @version 1.0
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The user details to generate the token for
     * @return A signed JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for the given user details.
     *
     * @param extraClaims Additional claims to include in the token
     * @param userDetails The user details to generate the token for
     * @return A signed JWT token as a string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates if a token is valid for the given user details.
     *
     * @param token The JWT token to validate
     * @param userDetails The user details to validate against
     * @return true if the token is valid for the user, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            logger.debug("Validating token for user: {}", username);
            
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            logger.debug("Token validation result: {} (username match: {}, not expired: {})", 
                isValid, 
                username.equals(userDetails.getUsername()),
                !isTokenExpired(token));
            
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token", e);
            return false;
        }
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token The JWT token to extract the username from
     * @return The username (subject) from the token
     */
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            return null;
        }
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean isExpired = expiration.before(new Date());
            logger.debug("Token expiration check: {} (expiration: {}, current: {})", 
                isExpired, expiration, new Date());
            return isExpired;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            return true;
        }
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token to extract the expiration from
     * @return The expiration date from the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from a JWT token using a claims resolver function.
     *
     * @param token The JWT token to extract the claim from
     * @param claimsResolver The function to resolve the specific claim
     * @return The resolved claim value
     * @param <T> The type of the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token to extract claims from
     * @return All claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Gets the signing key for JWT operations.
     * The key is generated from the secret key configured in the application properties.
     *
     * @return The signing key for JWT operations
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 