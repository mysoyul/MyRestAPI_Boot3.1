package com.basic.myrestapi.lectures;

import com.basic.myrestapi.lectures.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    List<Lecture> findByName(String name);
}
