package seoulmate.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seoulmate.mission.entity.Mission;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate,
                                                                         LocalDate endDate);
}
