package com.basic.myrestapi.lectures;

import com.basic.myrestapi.lectures.dto.LectureReqDto;
import com.basic.myrestapi.lectures.dto.LectureResDto;
import com.basic.myrestapi.lectures.entity.Lecture;
import com.basic.myrestapi.lectures.hateoas.LectureResource;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        //free 와 offline 값을 설정
        lecture.update();
        Lecture addLecture = this.lectureRepository.save(lecture);
        //Entity => ResDto
        LectureResDto lectureResDto = modelMapper.map(addLecture, LectureResDto.class);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(LectureController.class) //WebMvcLinkBuilder
                        .slash(lectureResDto.getId());
        URI createUri = selfLinkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));

        return ResponseEntity.created(createUri).body(lectureResource);
    }

    private static ResponseEntity<Errors> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }
}