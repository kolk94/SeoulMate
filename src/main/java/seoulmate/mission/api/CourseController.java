package seoulmate.mission.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import seoulmate.mission.entity.Course;
import seoulmate.mission.service.CourseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courseService.create(course);
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return courseService.get(id);
    }

    @GetMapping
    public List<Course> getAll() {
        return courseService.getAll();
    }
}
