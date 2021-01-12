package nextstep.subway.section.application;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SectionDistance {

    @Column(name = "distance")
    private int value;

    SectionDistance() {
    }

    public SectionDistance(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
