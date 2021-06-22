package nextstep.subway.exception;

public class LimitDistanceException extends RuntimeException {

    public static final String MESSAGE = "거리가 기준 거리 이하가 될 수 없습니다.";

    private final int min;

    public LimitDistanceException(int min) {
        this.min = min;
    }

    @Override
    public String getMessage() {
        return MESSAGE + " (기준 거리 : " + min + ")";
    }
}
