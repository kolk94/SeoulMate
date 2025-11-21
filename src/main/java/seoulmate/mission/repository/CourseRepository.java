package seoulmate.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seoulmate.mission.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
