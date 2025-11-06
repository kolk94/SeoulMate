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
import seoulmate.image.ImageStorageService;
import seoulmate.mission.entity.MissionAttempt;
import seoulmate.mission.service.MissionAttemptService;
import seoulmate.user.entity.User;
import seoulmate.user.repository.UserRepository;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionAttemptController {

    private final MissionAttemptService missionAttemptService;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    @PostMapping("/{missionId}/attempts")
    public MissionAttemptResponse createAttempt(
            @PathVariable Long missionId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @AuthenticationPrincipal UserDetails principal
    ) throws IOException {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String imageUrl = imageStorageService.store(image);

        MissionAttempt attempt = missionAttemptService.createAttempt(
                user.getId(),
                missionId,
                imageUrl,
                latitude,
                longitude
        );

        return new MissionAttemptResponse(
                attempt.getId(),
                attempt.getStatus().name(),
                attempt.getImageUrl(),
                attempt.getLatitude(),
                attempt.getLongitude()
        );
    }

    public record MissionAttemptResponse(
            Long id,
            String status,
            String imageUrl,
            double latitude,
            double longitude
    ) {
    }
}
