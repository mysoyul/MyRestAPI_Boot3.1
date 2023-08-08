package com.basic.myrestapi.lectures;

import org.springframework.data.jpa.repository.JpaRepository;
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
}
