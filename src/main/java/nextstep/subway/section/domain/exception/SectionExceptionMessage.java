package nextstep.subway.section.domain.exception;

public enum SectionExceptionMessage {
    NOT_FOUND_SECTION_BY_STATION("역이 포함된 구간이 없습니다."),
    NEW_SECTION_DISTANCE_IS_GREATER_OR_EQUALS("새로운 구간의 길이가 기존 구간의 길이보다 크거나 같습니다."),
    NOT_FOUND_UP_STATION_BY_SECTION("상행역이 존재하지 않습니다."),
    NOT_FOUND_FIRST_STATION("상행선 종점역이 없습니다."),
    NOT_FOUND_SECTION("해당되는 구간을 찾지 못하였습니다");

    String message;

    SectionExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
