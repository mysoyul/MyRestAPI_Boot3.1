package com.basic.myrestapi.lectures.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of="id")
@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;
    private String description;

    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginLectureDateTime;
    private LocalDateTime endLectureDateTime;

    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;

    private boolean free;

    @Enumerated(EnumType.STRING)
    private LectureStatus lectureStatus = LectureStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    //@ColumnDefault(value = "CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
}    