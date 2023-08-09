package com.basic.myrestapi.lectures.hateoas;

import com.basic.myrestapi.lectures.LectureController;
import com.basic.myrestapi.lectures.dto.LectureResDto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LectureResource extends RepresentationModel<LectureResource> {
    @JsonUnwrapped
    private final LectureResDto lectureResDto;
    public LectureResource(LectureResDto resDto) {
        this.lectureResDto = resDto;
        add(linkTo(LectureController.class).slash(resDto.getId()).withSelfRel());
    }
    public LectureResDto getLectureResDto() {
        return lectureResDto;
    }

}