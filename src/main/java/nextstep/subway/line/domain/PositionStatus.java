package nextstep.subway.line.domain;

public enum PositionStatus {
    FIRST(true, false, false),
    MIDDLE(false, true, false),
    LAST(false, false, true);

    private boolean isFirst;
    private boolean isMiddle;
    private boolean isLast;

    PositionStatus(boolean isFirst, boolean isMiddle, boolean isLast) {
        this.isFirst = isFirst;
        this.isMiddle = isMiddle;
        this.isLast = isLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isMiddle() {
        return isMiddle;
    }

    public boolean isLast() {
        return isLast;
    }
}
