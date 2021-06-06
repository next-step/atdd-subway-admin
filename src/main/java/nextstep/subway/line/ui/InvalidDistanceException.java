package nextstep.subway.line.ui;

public class InvalidDistanceException extends RuntimeException {

    public InvalidDistanceException() {
        super("등록하는 구간의 거리가 잘못되었습니다");
    }
}
