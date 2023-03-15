package ru.netology.netologydiploma.security.repository;


import ru.netology.netologydiploma.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
