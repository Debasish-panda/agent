package com.democommerce.backend.auth;

import com.democommerce.backend.ApiDtos;
import com.democommerce.backend.AppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    public AuthenticationController(AuthenticationManager authenticationManager, JwtService jwt) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public ApiDtos.LoginResponse login(@Valid @RequestBody ApiDtos.LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            AppUser user = (AppUser) authentication.getPrincipal();
            return new ApiDtos.LoginResponse(jwt.createToken(user), toView(user));
        } catch (AuthenticationException exception) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @GetMapping("/me")
    public ApiDtos.UserView me(@AuthenticationPrincipal AppUser user) { return toView(user); }

    private ApiDtos.UserView toView(AppUser user) {
        return new ApiDtos.UserView(user.id, user.email, user.role, user.tenantId);
    }
}
