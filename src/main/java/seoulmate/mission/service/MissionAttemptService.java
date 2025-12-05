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

        MissionAttempt existing = missionAttemptRepository.findByUser_IdAndMission_Id(userId, missionId)
                                                          .orElse(null);

        if (existing != null && existing.isSuccess()) {
            throw new IllegalStateException("Mission already completed");
        }

        if (existing == null) {
            MissionAttempt attempt = MissionAttempt.builder()
                                                   .user(user)
                                                   .mission(mission)
                                                   .latitude(latitude)
                                                   .longitude(longitude)
                                                   .success(false)
                                                   .build();
            return missionAttemptRepository.save(attempt);
        }

        existing.updateLocation(latitude, longitude);
        return existing;
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

        Mission mission = attempt.getMission();

        double distance = distanceMeters(
                attempt.getLatitude(),
                attempt.getLongitude(),
                mission.getLatitude(),
                mission.getLongitude()
        );

        if (distance > mission.getRadiusMeters()) {
            attempt.verifyFail("OUT_OF_RADIUS", 0.0);
            return attempt;
        }

        var result = imageAiService.classifyImage(imageBytes);

        String missionCategory = mission.getCategory();
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

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
