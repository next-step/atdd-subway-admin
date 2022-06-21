package nextstep.subway.exception;

public class InvalidDistanceException extends IllegalArgumentException {
    private static final String message = "노선 사이에 역을 추가할 경우 거리가 기존 거리(%d)보다 짧아야 합니다.";

    public InvalidDistanceException(Long distance) {
        super(String.format(message, distance));
    }
}
