package nextstep.subway.exception;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException() {
        super("노신이 존재하지 않습니다");
    }

}
