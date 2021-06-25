package nextstep.subway.section.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int value;

    protected Distance() {

    }

    public Distance(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if(value <= 0) {
            throw new IllegalArgumentException("거리는 0이하가 될 수 없습니다.");
        }
    }
}
