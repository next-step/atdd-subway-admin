package nextstep.subway.section.domain;

public enum DockingPosition {
    FRONT, REAR, NONE;

    private int index;

    public int index() {
        if (this.equals(FRONT)) {
            return this.index;
        }
        if (this.equals(REAR)) {
            return this.index+1;
        }
        throw new IllegalStateException();
    }

    public DockingPosition setIndex(int index) {
        this.index = index;
        return this;
    }
}
