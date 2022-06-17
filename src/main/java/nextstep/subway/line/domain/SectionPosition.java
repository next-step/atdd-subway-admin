package nextstep.subway.line.domain;

public enum SectionPosition {
    FRONT(0), BACK(1), NONE(-1);

    private final int offset;

    SectionPosition(int offset) {
        this.offset = offset;
    }

    public int calculateOffset(int currentIndex) {
        return currentIndex + offset;
    }
}
