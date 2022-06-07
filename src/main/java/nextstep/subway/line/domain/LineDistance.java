package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineDistance {
    @Column(name = "distance", nullable = false)
    private int value;

    protected LineDistance() {
    }

    public LineDistance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
    }

    public int value() {
        return value;
    }
}
