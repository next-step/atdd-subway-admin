package nextstep.subway.exception;

public class SectionSortingException extends RuntimeException{

    public static final String SECTION_SORTING_EXCEPTION = "노선 구간 정렬 중 오류가 발생했습니다.";

    public SectionSortingException() {
        super(SECTION_SORTING_EXCEPTION);
    }
}
