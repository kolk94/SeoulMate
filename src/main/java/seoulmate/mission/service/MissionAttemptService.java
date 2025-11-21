package seoulmate.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seoulmate.ai.ImageAiService;
import seoulmate.ai.model.ImageCategory;
import seoulmate.mission.entity.Mission;
import seoulmate.mission.entity.MissionAttempt;
import seoulmate.mission.repository.MissionAttemptRepository;
import seoulmate.mission.repository.MissionRepository;
import seoulmate.user.entity.User;
import seoulmate.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MissionAttemptService {

    private final MissionAttemptRepository missionAttemptRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final ImageAiService imageAiService;

    @Transactional
    public MissionAttempt createAttempt(Long userId,
                                        Long missionId,
                                        double latitude,
                                        double longitude) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found"));

        MissionAttempt attempt = MissionAttempt.builder()
                .user(user)
                .mission(mission)
                .success(false)
                .build();

        return missionAttemptRepository.save(attempt);
    }

    @Transactional
    public MissionAttempt verifySuccess(Long attemptId, String label, Double score) {
        MissionAttempt attempt = missionAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        attempt.verifySuccess(label, score);
        return attempt;
    }

    @Transactional
    public MissionAttempt verifyFail(Long attemptId, String label, Double score) {
        MissionAttempt attempt = missionAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        attempt.verifyFail(label, score);
        return attempt;
    }

    @Transactional
    public MissionAttempt analyzeAndVerify(Long attemptId, byte[] imageBytes) {
        MissionAttempt attempt = missionAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));

        var result = imageAiService.classifyImage(imageBytes);

        String missionCategory = attempt.getMission().getCategory();
        ImageCategory expectedCategory;
        try {
            expectedCategory = ImageCategory.valueOf(missionCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            expectedCategory = ImageCategory.OTHER;
        }

        if (result.category() == expectedCategory) {
            attempt.verifySuccess(result.label(), result.score());
        } else {
            attempt.verifyFail(result.label(), result.score());
        }

        return attempt;
    }
}
