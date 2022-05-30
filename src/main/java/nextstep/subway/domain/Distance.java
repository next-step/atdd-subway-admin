package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private Long value;

    protected Distance() {

    }

    public Distance(Long distance) {

        this.value = distance;
    }

    public Long getValue() {
        return value;
    }
}
