package com.basic.myrestapi.lectures;

import com.basic.myrestapi.common.exception.BusinessException;
import com.basic.myrestapi.common.hateoas.ErrorsResource;
import com.basic.myrestapi.lectures.dto.LectureReqDto;
import com.basic.myrestapi.lectures.dto.LectureResDto;
import com.basic.myrestapi.lectures.entity.Lecture;
import com.basic.myrestapi.lectures.hateoas.LectureResource;
import com.basic.myrestapi.lectures.validator.LectureValidator;
import com.basic.myrestapi.security.userinfo.UserInfo;
import com.basic.myrestapi.security.userinfo.annot.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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

    @PutMapping("/{id}")
    public ResponseEntity updateLecture(@PathVariable Integer id,
                                        @RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors,
                                        @CurrentUser UserInfo currentUser) {
        Lecture existingLecture = this.lectureRepository.findById(id) //Optional<Lecture>
                .orElseThrow(() -> new BusinessException(id + " Lecture Not Found", HttpStatus.NOT_FOUND));

        //입력항목의 값 자체를 검증
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        //입력항목을 값을 biz logic으로 검증
        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        
        //등록된 user 와 토큰의 user 와 비교해서 등록한 user 만 update 할 수 있도록 , user 가 다르다면 인증오류
        if((existingLecture.getUserInfo() != null) && (!existingLecture.getUserInfo().equals(currentUser))) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        //ReqDto => Entity
        this.modelMapper.map(lectureReqDto, existingLecture);
        existingLecture.update();
        Lecture savedLecture = this.lectureRepository.save(existingLecture);

        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        if(savedLecture.getUserInfo() != null)
            lectureResDto.setEmail(savedLecture.getUserInfo().getEmail());

        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")  //user@aa.com, admin2@aa.com 조회 가능
    public ResponseEntity getLecture(@PathVariable Integer id,
                                     @CurrentUser UserInfo currentUser) {
//        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
//        if(optionalLecture.isEmpty()) {
//            return ResponseEntity.notFound().build(); //404
//        }
//        Lecture lecture = optionalLecture.get();

        Lecture lecture = this.lectureRepository.findById(id) //Optional<Lecture>
                .orElseThrow(() -> new BusinessException(id + " Lecture Not Found", HttpStatus.NOT_FOUND));

        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        if(lecture.getUserInfo() != null)
            lectureResDto.setEmail(lecture.getUserInfo().getEmail());

        LectureResource lectureResource = new LectureResource(lectureResDto);
        //인증토큰의 email과 Lecture가 참조하는 email주소가 같으면 update링크를 제공하기
        if((lecture.getUserInfo() != null) && (lecture.getUserInfo().equals(currentUser))) {
            lectureResource.add(linkTo(LectureController.class)
                    .slash(lecture.getId()).withRel("update-lecture"));
        }
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  //admin@aa.com, admin2@aa.com 조회 가능
    public ResponseEntity queryLectures(Pageable pageable,
                                        PagedResourcesAssembler<LectureResDto> assembler,
                                        @CurrentUser UserInfo currentUser) {
        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);
        //Page<Lecture> => Page<LectureResDto>
        //Page<LectureResDto> lectureResDtoPage = lecturePage.map(entity -> modelMapper.map(entity, LectureResDto.class));

        //LectureResDto에  LectureResDto에 user정보 저장하기 위해 아래와 같이 변경
        Page<LectureResDto> lectureResDtoPage =
                lecturePage.map(lecture -> {
                    LectureResDto lectureResDto = new LectureResDto();
                    if(lecture.getUserInfo() != null){
                        lectureResDto.setEmail(lecture.getUserInfo().getEmail());
                    }
                    modelMapper.map(lecture, lectureResDto);
                    return lectureResDto;
                });

        //PagedModel<EntityModel<LectureResDto>> pagedModel = assembler.toModel(lectureResDtoPage);
        /*
            toModel(Page<T> page, RepresentationModelAssembler<T, R> assembler)
            RepresentationModelAssembler<T, D extends RepresentationModel<?>>
            함수형 인터페이스의 추상메서드 D toModel(T entity);
         */
        PagedModel<LectureResource> pagedModel =
                assembler.toModel(lectureResDtoPage, LectureResource::new);
                //assembler.toModel(lectureResDtoPage, resDto -> new LectureResource(resDto));
        //인증토큰이 있으면 Lecture 등록할 수 있는 Link 추가한다.
        if(currentUser != null) {
            pagedModel.add(linkTo(LectureController.class).withRel("create-Lecture"));
        }
        return ResponseEntity.ok(pagedModel);
    }
    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors,
                                        @CurrentUser UserInfo currentUser) {
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
        //Lecture와 UserInfo 참조 저장
        if(currentUser != null) lecture.setUserInfo(currentUser);

        Lecture addLecture = this.lectureRepository.save(lecture);
        //Entity => ResDto
        LectureResDto lectureResDto = modelMapper.map(addLecture, LectureResDto.class);
        //LectureResDto에 user정보 저장
        lectureResDto.setEmail(addLecture.getUserInfo().getEmail());

        WebMvcLinkBuilder selfLinkBuilder = linkTo(LectureController.class) //WebMvcLinkBuilder
                        .slash(lectureResDto.getId());
        URI createUri = selfLinkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
        lectureResource.add(selfLinkBuilder.withRel("update-lecture"));

        return ResponseEntity.created(createUri).body(lectureResource);
    }

    private static ResponseEntity<?> badRequest(Errors errors) {

        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}