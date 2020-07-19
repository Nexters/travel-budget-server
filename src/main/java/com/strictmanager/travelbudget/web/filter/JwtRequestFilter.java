package com.strictmanager.travelbudget.web.filter;

import com.strictmanager.travelbudget.infra.auth.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (Objects.isNull(requestTokenHeader) || !requestTokenHeader.startsWith("Bearer ")) {
            log.warn("JWT Token does not begin with Bearer String");
            chain.doFilter(request, response);
            return;
        }

        final String jwtToken = requestTokenHeader.substring(7);
        String userId = null;
        try {
            userId = jwtTokenUtil.getUsernameFromToken(jwtToken);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to get JWT Token");
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token has expired");
        }

        if (Objects.isNull(userId)) {
            log.warn("JWT Token does not have userId");
            chain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userService.loadUserByUsername(userId);
        if (Objects.nonNull(userDetails) && jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
        }

        chain.doFilter(request, response);
    }
}
