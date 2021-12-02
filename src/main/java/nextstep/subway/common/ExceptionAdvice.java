package nextstep.subway.common;

import java.util.NoSuchElementException;
import nextstep.subway.common.exception.IllegalDistanceException;
import nextstep.subway.common.exception.IllegalSectionRemoveException;
import nextstep.subway.common.exception.IllegalStationException;
import nextstep.subway.common.exception.LineNotFoundException;
import nextstep.subway.common.exception.LinkableSectionNotFoundException;
import nextstep.subway.common.exception.SectionNotFoundException;
import nextstep.subway.common.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity handleDataIntegrityViolationException(
        DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(value = {LineNotFoundException.class, StationNotFoundException.class})
    protected ResponseEntity handleNotFoundException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(value = {IllegalDistanceException.class, IllegalStationException.class})
    protected ResponseEntity handleIllegalExceptionCases(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(LinkableSectionNotFoundException.class)
    protected ResponseEntity handleIllegalExceptionCases(LinkableSectionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalSectionRemoveException.class)
    protected ResponseEntity handleIllegalSectionRemoveException(IllegalSectionRemoveException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(SectionNotFoundException.class)
    protected ResponseEntity handleSectionNotFoundException(SectionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
