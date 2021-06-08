package nextstep.subway.common.error;

import nextstep.subway.line.application.LineDuplicatedException;
import nextstep.subway.line.application.LineNotFoundException;
import nextstep.subway.section.application.SectionDuplicatedException;
import nextstep.subway.section.application.StationNotRegisterException;
import nextstep.subway.station.application.StationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class ErrorAdvice {
    private static final String LINE_ALREADY_EXISTED = "이미 존재하는 노선을 생성할 수 없습니다";
    private static final String SECTION_ALREADY_EXISTED = "이미 존재하는 구간을 생성할 수 없습니다";
    private static final String LINE_NOT_FOUND = "존재하지 않은 노선입니다.";
    private static final String STATION_NOT_REGISTERED = "등록되지 않은 역입니다.";
    private static final String STATION_NOT_FOUND = "존재하지 않은 역입니다.";
    private static final String INVALID_INPUT = "잘못된 입력입니다.";

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LineDuplicatedException.class)
    public ErrorResponse handleLineDuplicated() {
        return new ErrorResponse(LINE_ALREADY_EXISTED);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LineNotFoundException.class)
    public ErrorResponse handleLineNotFound() {
        return new ErrorResponse(LINE_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StationNotFoundException.class)
    public ErrorResponse handleStationNotFound() {
        return new ErrorResponse(STATION_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument() {
        return new ErrorResponse(INVALID_INPUT);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SectionDuplicatedException.class)
    public ErrorResponse handleSectionDuplicated() {
        return new ErrorResponse(SECTION_ALREADY_EXISTED);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StationNotRegisterException.class)
    public ErrorResponse handleStationNotRegistered() {
        return new ErrorResponse(STATION_NOT_REGISTERED);
    }
}
