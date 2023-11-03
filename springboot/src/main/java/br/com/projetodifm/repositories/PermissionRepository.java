package br.com.projetodifm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projetodifm.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>{

}
