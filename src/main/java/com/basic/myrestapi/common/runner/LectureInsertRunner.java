package com.basic.myrestapi.common.runner;

import com.basic.myrestapi.lectures.LectureRepository;
import com.basic.myrestapi.lectures.entity.Lecture;
import com.basic.myrestapi.lectures.entity.LectureStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
@Profile("prod") //나중에 변경 필요
public class LectureInsertRunner implements ApplicationRunner {
	@Autowired
    LectureRepository lectureRepository;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		IntStream.rangeClosed(1, 15) //IntStream
                .forEach(this::generateLecture);
                //.forEach(val -> generateLecture(val));
	}
    private Lecture generateLecture(int index) {
        Lecture lecture = buildLecture(index);
        return this.lectureRepository.save(lecture);
    }
    private Lecture buildLecture(int index) {
        return Lecture.builder()
                    .name(index + " Lecture ")
                    .description("Test Lecture")
                    .beginEnrollmentDateTime(LocalDateTime.of(2022, 11, 23, 14, 21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2022, 11, 24, 14, 21))
                    .beginLectureDateTime(LocalDateTime.of(2022, 11, 25, 14, 21))
                    .endLectureDateTime(LocalDateTime.of(2022, 11, 26, 14, 21))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location(index + " 강의장")
                    .free(false)
                    .offline(true)
                    .lectureStatus(LectureStatus.DRAFT)
                    .build();
    }
}