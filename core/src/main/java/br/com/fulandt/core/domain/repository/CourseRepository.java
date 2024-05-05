package br.com.fulandt.core.domain.repository;


import br.com.fulandt.core.domain.entity.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {
}
