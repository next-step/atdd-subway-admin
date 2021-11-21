package nextstep.subway.line.domain;

public enum SectionMatchingType {
    UP_AND_UP(1),
    UP_AND_DOWN(2),
    DOWN_AND_UP(3),
    DOWN_AND_DOWN(4),
    UNKNOWN(0);

    private final Integer value;

    SectionMatchingType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
