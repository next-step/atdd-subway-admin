package nextstep.subway.distance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Distance {
    private static final int EMPTY = 0;
    private long distance;

    public Distance(final long distance) {
        if (EMPTY >= distance) {
            throw new IllegalArgumentException();
        }

        this.distance = distance;
    }
}
