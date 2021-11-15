package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    @Column(name = "distance")
    private Integer value;

    private Distance(Integer value) {
        validate(value);

        this.value = value;
    }

    public static Distance valueOf(Integer value) {
        return new Distance(value);
    }

    public void validate(Integer value) {
        checkNegativeValue(value);
        checkZeroValue(value);
    }

    private void checkZeroValue(Integer value) {
        if(value == 0) {
            throw new IllegalArgumentException("0값이 입력되었습니다.");
        }
    }

    private void checkNegativeValue(Integer value) {
        if(value < 0) {
            throw new IllegalArgumentException("음수값이 입력되었습니다.");
        }
    }

    public Integer value() {
        return this.value;
    }
}
