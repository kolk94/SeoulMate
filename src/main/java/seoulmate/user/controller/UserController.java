package seoulmate.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seoulmate.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody RegisterRequest request) {
        Long userId = userService.register(
                request.email(),
                request.password(),
                request.nickname()
        );
        return ResponseEntity.ok(userId);
    }

    public record RegisterRequest(
            String email,
            String password,
            String nickname
    ) {
    }
}
