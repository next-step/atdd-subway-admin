package nextstep.subway.exception;

public class DuplicatedSectionException extends IllegalArgumentException {
    private static final String message = "상행역(%d)과 하행역(%d)이 이미 노선에 등록되어 있습니다.";

    public DuplicatedSectionException(Long upStationId, Long downStationId) {
        super(String.format(message, upStationId, downStationId));
    }
}
