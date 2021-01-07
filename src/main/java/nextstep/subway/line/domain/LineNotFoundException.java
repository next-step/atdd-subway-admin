package nextstep.subway.line.domain;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException(Long id) {
        super("Could not find line " + id);
    }

}
