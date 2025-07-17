package com.keepgoing.keepserver.domain.user.domain.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        이메일
    */
    @Column(nullable = false)
    private String email;

    /*
        패스워드
    */
    @Column(nullable = false)
    private String password;

    /*
        이름
    */
    @Column(nullable = false)
    private String name;

    /*
        교사 여부
    */
    @Column(nullable = false)
    private boolean teacher;

    /*
        교사 계정 승인 여부
     */
    @Column(nullable = false)
    private boolean approved;

    /*
        생성 시간
     */
    @Column(nullable = false)
    private LocalDateTime createAt;

    public static User registerUser(
            String email,
            String password,
            String name,
            boolean teacher,
            boolean approved
    ) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.name = name;
        user.teacher = teacher;
        user.approved = approved;

        return user;
    }

    public void fixUserData(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
