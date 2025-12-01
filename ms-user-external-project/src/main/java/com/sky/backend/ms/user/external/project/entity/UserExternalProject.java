package com.sky.backend.ms.user.external.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_user_external_project")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserExternalProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 120)
    private String name;
}
