package com.example.spring_study.repository;

import com.example.spring_study.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);

    @Query(value = "SELECT * FROM role r WHERE r.name IN (:names)", nativeQuery = true)
    Set<Role> findByNameIn(@Param("names") Set<String> names);
}
