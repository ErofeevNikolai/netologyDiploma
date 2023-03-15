package ru.netology.netologydiploma.security.repository;


import ru.netology.netologydiploma.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(String Role);
}
