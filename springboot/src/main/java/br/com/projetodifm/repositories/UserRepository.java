package br.com.projetodifm.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projetodifm.model.User;
import br.com.projetodifm.model.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
            select u from User u inner join Token t on t.user.id = u.id
            where t.token = :token and (t.expired = false or t.revoked = false)
            """)
    Optional<User> findByToken(@Param(value = "token") String token);

    Boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    Page<User> findByPermissions(Permission permission, Pageable pageable);

    Boolean existsByIdAndPermissions(Long id, Permission permission);
}
