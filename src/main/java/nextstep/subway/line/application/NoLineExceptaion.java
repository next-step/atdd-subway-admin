package nextstep.subway.line.application;

public class NoLineExceptaion extends RuntimeException {
    public NoLineExceptaion(String message) {
        super(message);
    }
}
