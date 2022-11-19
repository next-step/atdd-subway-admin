package nextstep.subway.section;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private Long distance;

    protected Distance() { }

    public Distance(Long distance) {
        this.distance = distance;
    }
}
