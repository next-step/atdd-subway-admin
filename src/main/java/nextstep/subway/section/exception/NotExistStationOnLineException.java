package nextstep.subway.section.exception;

public class NotExistStationOnLineException extends RuntimeException {

    public NotExistStationOnLineException() {
        super("해당 노선에 삭제하려는 역이 등록되어 있지 않습니다.");
    }
}
