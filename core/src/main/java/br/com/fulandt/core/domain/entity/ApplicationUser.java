package br.com.fulandt.core.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="applicationUser")
public class ApplicationUser implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true)
    private Long id;

    @NotNull(message = "username e obrigatorio")
    @Column(nullable = false)
    private String username;

    @ToString.Exclude
    @NotNull(message = "senha e obrigatorio")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "roles e obrigatorio")
    @Column(nullable = false)
    private String role = "USER"; // mais de uma role private String role = "USER, ADMIN"

    //CONSTRUTOR PARA COPY
    public ApplicationUser (@NotNull ApplicationUser applicationUser){
        this.id = applicationUser.getId();
        this.username = applicationUser.getUsername();
        this.password = applicationUser.getPassword();
        this.role = applicationUser.getRole();

    }

}
