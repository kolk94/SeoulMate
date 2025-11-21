package seoulmate.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seoulmate.security.jwt.JwtTokenProvider;
import seoulmate.user.entity.User;
import seoulmate.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody RegisterRequest request) {
        Long userId = userService.register(
                request.email(),
                request.password(),
                request.nickname()
        );
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.email(), request.password());
        String token = jwtTokenProvider.createToken(user.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    public record RegisterRequest(
            String email,
            String password,
            String nickname
    ) {}

    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token) {}
}
