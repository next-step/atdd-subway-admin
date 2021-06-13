package nextstep.subway.section.domain;

public class Position {
    private final DockingType dockingType;
    private int index = 0;

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

    public int positionIndex() {
        if (dockingType == DockingType.FRONT
                || dockingType == DockingType.MID_FRONT
                || dockingType == DockingType.MID_REAR) {
            return index;
        }
        return ++index;
    }

    public int index() {
        return index;
    }

    public int nextIndex() {
        return ++index;
    }

    public void subIndex() {
        --index;
    }

    public boolean isNotDockedYet() {
        return DockingType.NONE == dockingType;
    }
}
