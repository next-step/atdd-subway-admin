package nextstep.subway.exception;

public enum SectionsExceptionMessage  {
    LONGER_THAN_OHTER("길이가 작은 구간만 추가할 수 있습니다");
    String message;

    SectionsExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
