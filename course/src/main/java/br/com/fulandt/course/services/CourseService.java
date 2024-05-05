package br.com.fulandt.course.services;

import br.com.fulandt.core.domain.entity.Course;
import br.com.fulandt.core.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseService {
    private final CourseRepository courseRepository;

    public Iterable<Course> list (Pageable pageable){
        log.info("Listem all courses");
        return courseRepository.findAll(pageable);
    }
    public  Course save(Course course){
       return courseRepository.save(course);
    }


}
