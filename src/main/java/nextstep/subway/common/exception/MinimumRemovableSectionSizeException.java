package nextstep.subway.common.exception;

public class MinimumRemovableSectionSizeException extends RuntimeException {
    public static final String MINIMUM_REMOVABLE_SECTION_SIZE_MESSAGE = "삭제할 수 없는 구간 크기 입니다. : ";
    private static final long serialVersionUID = 10L;

    public MinimumRemovableSectionSizeException(int size) {
        super(MINIMUM_REMOVABLE_SECTION_SIZE_MESSAGE + size);
    }
}
