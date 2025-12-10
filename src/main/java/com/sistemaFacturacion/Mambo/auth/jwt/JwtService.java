package com.sistemaFacturacion.Mambo.auth.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.sistemaFacturacion.Mambo.entity.model.Usuario;
import com.sistemaFacturacion.Mambo.entity.model.cliente;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {
    private static final String SECRET_KEY = "586E3272357538782F413F4428472B4B6250655368566B597033733676397924";

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String getToken(Usuario user) {
        UserDetails userDetails = buildUserDetails(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_" + user.getRol().getNombre()); 
        return generateToken(extraClaims, userDetails);
    }

    public String getTokenCliente(cliente cliente) {
        UserDetails userDetails = buildClienteUserDetails(cliente);
        
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ROLE_" + cliente.getRol().getNombre());
        
        return generateToken(extraClaims, userDetails);
    }


    private UserDetails buildUserDetails(Usuario user) {
        return new User(
            user.getNumeroDocumento(),
            user.getContra(),
            user.isEnabled(),
            true, true, true,
            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRol().getNombre()))
        );
    }

    private UserDetails buildClienteUserDetails(cliente cliente) {
        return new User(
            cliente.getNumeroDocumento(),
            cliente.getContra(),
            cliente.isEnabled(),
            true, true, true,
            List.of(new SimpleGrantedAuthority("ROLE_" + cliente.getRol().getNombre()))
        );
    }


    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) 
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }


     // Verifica si el token pertenece a un usuario válido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

     private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

}
