package nextstep.subway.error;

public class CanNotRemovableSectionException extends RuntimeException{

    private static final String MESSAGE = "can not remove section.";

    public CanNotRemovableSectionException() {
        super(MESSAGE);
    }
}
