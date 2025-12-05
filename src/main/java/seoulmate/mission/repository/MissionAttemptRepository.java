package seoulmate.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seoulmate.mission.entity.MissionAttempt;

import java.util.List;
import java.util.Optional;

public interface MissionAttemptRepository extends JpaRepository<MissionAttempt, Long> {

    List<MissionAttempt> findByUser_Id(Long userId);

    Optional<MissionAttempt> findByUser_IdAndMission_Id(Long userId, Long missionId);
}
