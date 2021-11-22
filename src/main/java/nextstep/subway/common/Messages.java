package nextstep.subway.common;

public enum Messages {
    NO_STATION("역정보가 없습니다."),
    NO_LINE("노선정보가 없습니다.");

    private String values;

    Messages(String values) {
        this.values = values;
    }

    public String getValues() {
        return values;
    }
}
