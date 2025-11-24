package com.pet.petCare.security;

import com.pet.petCare.repository.HospitalRepository;
import com.pet.petCare.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, HospitalRepository hospitalRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = getJwtFromRequest(request);

            if (token != null) {
                String username = jwtUtil.extractUsername(token);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    var userOptional = userRepository.findByUsername(username);

                    if (userOptional.isPresent()) {
                        var user = userOptional.get();
                        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        var hospitalOptional = hospitalRepository.findByUsername(username);

                        if (hospitalOptional.isPresent()) {
                            var hospital = hospitalOptional.get();
                            UserDetails hospitalDetails = new org.springframework.security.core.userdetails.User(
                                    hospital.getUsername(),
                                    hospital.getPassword(),
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOSPITAL"))
                            );

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(hospitalDetails, null, hospitalDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("사용자 인증 정보를 설정하는 중 오류가 발생했습니다.", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}