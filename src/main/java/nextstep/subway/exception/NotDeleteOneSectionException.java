package nextstep.subway.exception;

public class NotDeleteOneSectionException extends BadRequestException {
    private static final String ONE_SECTION_MESSAGE = "구간이 하나인 노선은 제거할 수 없습니다.";

    public NotDeleteOneSectionException() {
        super(ONE_SECTION_MESSAGE);
    }
}
