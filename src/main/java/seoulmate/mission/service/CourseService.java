package seoulmate.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seoulmate.mission.entity.Course;
import seoulmate.mission.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course create(Course course) {
        return courseRepository.save(course);
    }

    public Course get(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }
}
