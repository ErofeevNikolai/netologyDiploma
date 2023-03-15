package ru.netology.netologydiploma.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.netologydiploma.security.entity.AuthorizationStatus;
import ru.netology.netologydiploma.security.entity.User;

@Repository
public interface AuthorizationRepositoty extends JpaRepository<AuthorizationStatus, Long> {
    AuthorizationStatus findByToken(String token);

    AuthorizationStatus findByUser(User user);

    void  deleteAuthorizationStatusByUser(User user);

    void deleteAuthorizationStatusByToken(String token);

}
