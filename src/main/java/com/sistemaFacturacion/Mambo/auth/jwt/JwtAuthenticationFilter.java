package com.sistemaFacturacion.Mambo.auth.jwt;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        
        // LOG 1: Ver qué ruta se está pidiendo
        System.out.println("🔎 FILTER: Petición entrante a: " + path);

        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = getTokenFromRequest(request);

            if (token == null) {
                // LOG 2: Alerta si no hay token
                System.out.println("⚠️ FILTER: No se encontró token en la petición a " + path);
            } else {
                // LOG 3: Token encontrado
                System.out.println("✅ FILTER: Token recibido (inicia con): " + token.substring(0, Math.min(token.length(), 10)) + "...");
                
                String username = jwtService.getUsernameFromToken(token);
                System.out.println("👤 FILTER: Usuario extraído del token: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        System.out.println("🔓 FILTER: Token VÁLIDO. Autenticando usuario...");
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        System.out.println("❌ FILTER: Token INVÁLIDO para el usuario " + username);
                    }
                } else {
                    System.out.println("ℹ️ FILTER: Usuario ya autenticado o username nulo.");
                }
            }
        } catch (Exception e) {
            System.out.println("🔥 FILTER ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace(); // Ver el error completo
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // LOG EXTRA: Ver si llega el header crudo
        if (authHeader == null) {
             System.out.println("⚠️ FILTER: Header Authorization es NULL");
        } else {
             System.out.println("📨 FILTER: Header Authorization detectado.");
        }

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}