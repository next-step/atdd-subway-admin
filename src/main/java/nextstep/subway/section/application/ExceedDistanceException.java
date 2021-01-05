package nextstep.subway.section.application;

public class ExceedDistanceException extends RuntimeException {
    public ExceedDistanceException(Integer distance, Integer newDistance) {
        super("기존 구간 길이: " + distance + "입력한 구간 길이: " + newDistance);
    }
}
