package nextstep.subway.exception;

public enum SectionsExceptionMessage  {
    LONGER_THAN_OHTER("길이가 작은 구간만 추가할 수 있습니다"),
    ALREADY_CONTAINS_SECTION("이미 같은 구간이 존재합니다"),
    NOT_CONSTAINS_ANY_SECTION("포함된 구간이 없습니다"),
    EMPTY_SECTION("구간정보가 존재하지 않습니다");

    String message;

    SectionsExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
