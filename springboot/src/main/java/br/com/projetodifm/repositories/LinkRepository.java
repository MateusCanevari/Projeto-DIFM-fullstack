package br.com.projetodifm.repositories;

import br.com.projetodifm.model.Links;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Links, Long> {

    @Query("""
            select l from Links l where lower(l.link) like lower(concat('%', :link,'%'))
            and l.user.id = :user_id
            """)
    Optional<Links> findByURLAndUserId(@Param(value = "link") String link, @Param(value = "user_id") Long userId);
}
