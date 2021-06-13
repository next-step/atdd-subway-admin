package nextstep.subway.section.domain;

public class DockingPosition {
    private DockingType dockingType;
    private int index = 0;

    private DockingPosition(DockingType dockingType) {
        this.dockingType = dockingType;
    }

    public static DockingPosition none() {
        return new DockingPosition(DockingType.NONE);
    }

    protected static DockingPosition front() {
        return new DockingPosition(DockingType.FRONT);
    }

    protected static DockingPosition midFront() {
        return new DockingPosition(DockingType.MID_FRONT);
    }

    protected static DockingPosition midRear() {
        return new DockingPosition(DockingType.MID_REAR);
    }

    protected static DockingPosition rear() {
        return new DockingPosition(DockingType.REAR);
    }

    public DockingPosition isFront() {
        dockingType = DockingType.FRONT;
        return this;
    }

    public DockingPosition isRear() {
        dockingType = DockingType.REAR;
        return this;
    }

    public DockingPosition isMidFront() {
        dockingType = DockingType.MID_FRONT;
        return this;
    }

    public DockingPosition isMidRear() {
        dockingType = DockingType.MID_REAR;
        return this;
    }

    public DockingPosition isNone() {
        dockingType = DockingType.NONE;
        return this;
    }

    public int positionIndex() {
        if (dockingType == DockingType.FRONT || dockingType == DockingType.MID_FRONT) {
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
