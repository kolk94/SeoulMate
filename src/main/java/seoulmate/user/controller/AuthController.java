package seoulmate.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seoulmate.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginRequest request) {
        Long userId = userService.login(request.email(), request.password());
        return ResponseEntity.ok(userId);
    }

    public record LoginRequest(
            String email,
            String password
    ) {
    }
}
