package org.mabr.authenticationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.authenticationservice.dto.JwtRequest;
import org.mabr.authenticationservice.dto.JwtResponse;
import org.mabr.authenticationservice.service.JwtUtil;
import org.mabr.authenticationservice.service.UserDetailsServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping()
    public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest request) {
        try {

            var authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.username(), request.password()
            ));

            var user = (UserDetails) authenticate.getPrincipal();
            var token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<HttpStatus> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        log.info("Start token validating");
        var token = bearerToken.split(" ")[1].trim();
        var username = jwtUtil.extractUsername(token);
        var userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(token, userDetails)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
