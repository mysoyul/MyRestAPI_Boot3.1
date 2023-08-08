package com.basic.myrestapi.lectures.validator;

import java.time.LocalDateTime;

import com.basic.myrestapi.lectures.dto.LectureReqDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class LectureValidator {
	public void validate(LectureReqDto lectureReqDto, Errors errors) {
		//basePrice > maxPrice 크면 오류를 발생시킴
		if(lectureReqDto.getBasePrice() > lectureReqDto.getMaxPrice() &&
				lectureReqDto.getMaxPrice() != 0) {
			//Field Error
			errors.rejectValue("basePrice", "wrongValue",
					"BasePrice 는 MaxPrice 보다 작아야 합니다!");
			errors.rejectValue("maxPrice", "wrongValue",
					"MaxPrice 는 BasePrice 보다 커야 합니다!");
			//Global Error
			errors.reject("wrongPrices", "BasePrice와 MaxPrice의 값을 다시 확인하세요!");
		}
		
		LocalDateTime endLectureDateTime = lectureReqDto.getEndLectureDateTime();
		if(endLectureDateTime.isBefore(lectureReqDto.getBeginLectureDateTime()) ||
		   endLectureDateTime.isBefore(lectureReqDto.getCloseEnrollmentDateTime()) ||
		   endLectureDateTime.isBefore(lectureReqDto.getBeginEnrollmentDateTime()) ) {
			errors.rejectValue("endLectureDateTime", "wrongValue",
					"강의 종료일이 더 이후 날짜이어야 합니다.");
		}
	}
}