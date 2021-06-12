package nextstep.subway.section.domain;

public class Position {
    private DockingType dockingType;
    private int index = -1;

    private Position(DockingType dockingType) {
        this.dockingType = dockingType;
    }

    public static Position isFront() {
        return new Position(DockingType.FRONT);
    }

    public static Position isRear() {
        return new Position(DockingType.REAR);
    }

    public static Position isMidFront() {
        return new Position(DockingType.MID_FRONT);
    }

    public static Position isMidRear() {
        return new Position(DockingType.MID_REAR);
    }

    public static Position isNone() {
        return new Position(DockingType.NONE);
    }

    public int index() {
        if (dockingType == DockingType.FRONT
                || dockingType == DockingType.MID_FRONT
                || dockingType == DockingType.MID_REAR) {
            return index;
        }
        return ++index;
    }

    public void set(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("유효하지 않은 위치값");
        }
        this.index = position;
    }

    public boolean isNotDockedYet() {
        if (dockingType == DockingType.NONE) {
            return true;
        }
        return false;
    }
}
