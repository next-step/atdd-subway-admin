package nextstep.subway.domain;

import static sun.security.krb5.internal.ccache.FileCredentialsCache.checkValidation;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import nextstep.subway.value.ErrMsg;

@Embeddable
public class Distance {

    @Column(name = "distance", nullable = false)
    private int value;
    private static final int MIN_DISTANCE = 0;
    private Distance(final int value) {
        validateDistance(value);
        this.value = value;
    }

    protected Distance() {

    }

    public static Distance from(int distance){
        return new Distance(distance);
    }

    private void validateDistance(final int distance){
        if(distance<=MIN_DISTANCE){
            throw new IllegalArgumentException(ErrMsg.INAPPROPRIATE_DISTANCE);
        }
    }


    public Distance subtract(Distance distance) {
        return new Distance(this.value -= distance.value);
    }
    public Distance add(Distance distance) {
        return new Distance(this.value += distance.value);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance) o;
        return this.value == distance.getValue();
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


}
