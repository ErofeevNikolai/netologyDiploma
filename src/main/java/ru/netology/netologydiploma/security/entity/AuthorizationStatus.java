package ru.netology.netologydiploma.security.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "authorization_status")
@Data
public class AuthorizationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "token")
    private String token;

    public AuthorizationStatus(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public AuthorizationStatus() {

    }
}
