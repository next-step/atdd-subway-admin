package nextstep.subway.line.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private Integer distance;

    protected Distance() {
    }

    public Distance(Integer distance) {
        if (distance == null) {
            throw new IllegalArgumentException("distance를 입력하세요");
        }
        this.distance = distance;
    }

    public Integer value() {
        return distance;
    }

    public void minus(Integer distance) {
        this.distance = this.distance - distance;
    }
}
