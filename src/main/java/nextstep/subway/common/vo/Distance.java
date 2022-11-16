package nextstep.subway.common.vo;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int distance;

    protected Distance() {}

    public Distance(int distance) {
        this.distance = distance;
    }
}
