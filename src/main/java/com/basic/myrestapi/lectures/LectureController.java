package com.basic.myrestapi.lectures;

import com.basic.myrestapi.lectures.dto.LectureReqDto;
import com.basic.myrestapi.lectures.entity.Lecture;
import com.basic.myrestapi.lectures.validator.LectureValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    //Constructor Injection
//    public LectureController(LectureRepository lectureRepository) {
//        this.lectureRepository = lectureRepository;
//    }

    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors) {
        //입력항목의 값 자체를 검증
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        //입력항목을 값을 biz logic으로 검증
        this.lectureValidator.validate(lectureReqDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        //DTO => Entity
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        Lecture addLecture = this.lectureRepository.save(lecture);

        WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder
                        .linkTo(LectureController.class) //WebMvcLinkBuilder
                        .slash(addLecture.getId());

        URI createUri = selfLinkBuilder.toUri();
        return ResponseEntity.created(createUri).body(addLecture);
    }

    private static ResponseEntity<Errors> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }
}