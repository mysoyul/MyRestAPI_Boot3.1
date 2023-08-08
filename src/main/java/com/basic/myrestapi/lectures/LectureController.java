package com.basic.myrestapi.lectures;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
public class LectureController {
    private final LectureRepository lectureRepository;
    //Constructor Injection
//    public LectureController(LectureRepository lectureRepository) {
//        this.lectureRepository = lectureRepository;
//    }

    @PostMapping
    public ResponseEntity createLecture(@RequestBody Lecture lecture) {
        Lecture addLecture = this.lectureRepository.save(lecture);

        WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder
                        .linkTo(LectureController.class) //WebMvcLinkBuilder
                        .slash(addLecture.getId());

        URI createUri = selfLinkBuilder.toUri();
        return ResponseEntity.created(createUri).body(addLecture);
    }
}