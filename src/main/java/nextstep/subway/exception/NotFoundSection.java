package nextstep.subway.exception;

public class NotFoundSection extends BadRequestException {
    private static final String NOT_FOUND_SECTION_MESSAGE = "구간을 찾을 수 없습니다.";

    public NotFoundSection() {
        super(NOT_FOUND_SECTION_MESSAGE);
    }
}
