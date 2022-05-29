package nextstep.subway.error;

public class SectionNotFoundException extends RuntimeException {

    private static final String MESSAGE = "section not found.";

    public SectionNotFoundException() {
        super(MESSAGE);
    }

}
