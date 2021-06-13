package nextstep.subway.station.domain;

public class DeletePosition {

    private DeleteType type;
    int index = 0;

    private DeletePosition(DeleteType type) {
        this.type = type;
    }

    public static DeletePosition None() {
        return new DeletePosition(DeleteType.NONE);
    }

    public boolean isUpInHead() {
        return type == DeleteType.UP_IN_HEAD;
    }

    public boolean isUpInTail() {
        return type == DeleteType.UP_IN_TAIL;
    }

    public boolean isUpInMiddles() {
        return type == DeleteType.UP_IN_MIDDLES;
    }

    public boolean isDownInTail() {
        return type == DeleteType.DOWN_IN_TAIL;
    }

    public boolean isNone() {
        return type == DeleteType.NONE;
    }

    public int index() {
        return index;
    }

    public void nextIndex() {
        ++index;
    }

    public DeletePosition typeUpInHead() {
        type = DeleteType.UP_IN_HEAD;
        return this;
    }

    public DeletePosition typeUpInTail() {
        type = DeleteType.UP_IN_TAIL;
        return this;
    }

    public DeletePosition typeUpInMiddles() {
        type = DeleteType.UP_IN_MIDDLES;
        return this;
    }

    public DeletePosition typeDownInTail() {
        type = DeleteType.DOWN_IN_TAIL;
        return this;
    }

    public void subtractIndex() {
        --index;
    }

}