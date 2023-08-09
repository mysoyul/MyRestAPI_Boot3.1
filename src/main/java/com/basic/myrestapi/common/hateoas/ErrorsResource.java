package com.basic.myrestapi.common.hateoas;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    @JsonUnwrapped
    private Errors errors;

    public ErrorsResource(Errors content) {
        this.errors = content;
        //IndexController의 index() 메서드의 매핑정보로 Link 생성하기
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }

}