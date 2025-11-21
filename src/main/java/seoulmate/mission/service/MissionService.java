package seoulmate.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seoulmate.mission.entity.Mission;
import seoulmate.mission.repository.MissionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    public List<Mission> findNearbyActiveMissions(double userLat, double userLng, int maxDistanceMeters) {
        LocalDate today = LocalDate.now();
        List<Mission> activeMissions =
                missionRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);

        return activeMissions.stream()
                .filter(m -> distanceMeters(
                        userLat,
                        userLng,
                        m.getLatitude(),
                        m.getLongitude())
                        <= maxDistanceMeters)
                .toList();
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
