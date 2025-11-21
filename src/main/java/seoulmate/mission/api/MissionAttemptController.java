package seoulmate.mission.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import seoulmate.mission.entity.MissionAttempt;
import seoulmate.mission.service.MissionAttemptService;
import seoulmate.user.entity.User;
import seoulmate.user.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionAttemptController {

    private final MissionAttemptService missionAttemptService;
    private final UserRepository userRepository;

    @PostMapping("/{missionId}/attempts")
    public MissionAttemptResponse createAttempt(
            @PathVariable Long missionId,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails principal
    ) throws Exception {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MissionAttempt attempt = missionAttemptService.createAttempt(
                user.getId(),
                missionId,
                0,
                0
        );

        attempt = missionAttemptService.analyzeAndVerify(attempt.getId(), image.getBytes());

        return new MissionAttemptResponse(
                attempt.getId(),
                attempt.isSuccess()
        );
    }

    public record MissionAttemptResponse(
            Long id,
            boolean success
    ) {
    }
}
