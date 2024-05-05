package br.com.fulandt.core.domain.repository;


import br.com.fulandt.core.domain.entity.ApplicationUser;;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long> {

    public ApplicationUser findByUsername(String username);
}
