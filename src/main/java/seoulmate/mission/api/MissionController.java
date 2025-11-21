package seoulmate.mission.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seoulmate.mission.entity.Mission;
import seoulmate.mission.service.MissionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/nearby")
    public List<NearbyMissionResponse> getNearbyMissions(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(name = "radius", defaultValue = "500") int radiusMeters
    ) {
        List<Mission> missions = missionService.findNearbyActiveMissions(lat, lng, radiusMeters);
        return missions.stream()
                .map(m -> new NearbyMissionResponse(
                        m.getId(),
                        m.getTitle(),
                        m.getDescription(),
                        m.getCategory(),
                        m.getLocationName(),
                        m.getLatitude(),
                        m.getLongitude(),
                        m.getRadiusMeters()
                ))
                .toList();
    }

    @GetMapping("/course/{courseId}")
    public List<CourseMissionResponse> getMissionsByCourse(@PathVariable Long courseId) {
        return missionService.getMissionsByCourse(courseId).stream()
                .map(m -> new CourseMissionResponse(
                        m.getId(),
                        m.getTitle(),
                        m.getDescription(),
                        m.getCategory(),
                        m.getLocationName(),
                        m.getLatitude(),
                        m.getLongitude(),
                        m.getRadiusMeters(),
                        m.getMissionOrder()
                ))
                .toList();
    }

    public record NearbyMissionResponse(
            Long id,
            String title,
            String description,
            String category,
            String locationName,
            double latitude,
            double longitude,
            int radiusMeters
    ) {}

    public record CourseMissionResponse(
            Long id,
            String title,
            String description,
            String category,
            String locationName,
            double latitude,
            double longitude,
            int radiusMeters,
            int missionOrder
    ) {}
}
