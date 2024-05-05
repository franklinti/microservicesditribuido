package br.com.fulandt.course.controller;

import br.com.fulandt.core.domain.entity.Course;

import br.com.fulandt.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course")
@Slf4j
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class CourseController {

    private final CourseService courseService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Course> list (Pageable pageable){
        Iterable<Course> i = courseService.list(pageable);
        System.out.println(i);
        return i;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Course save(@RequestBody Course course){
        log.info("Salvando curso"+ course);
        return courseService.save(course);
    }
}
