package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private int value;

    public Distance() {
    }

    public Distance(int value) {
        this.value = value;
    }
}
