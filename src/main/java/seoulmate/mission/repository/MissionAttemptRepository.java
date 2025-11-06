package seoulmate.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seoulmate.mission.entity.MissionAttempt;

import java.util.List;

public interface MissionAttemptRepository extends JpaRepository<MissionAttempt, Long> {

    List<MissionAttempt> findByUser_Id(Long userId);

    List<MissionAttempt> findByUser_IdAndMission_Id(Long userId, Long missionId);
}
