package nextstep.subway.distance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Distance {
    private long distance;

    public Distance(final long distance) {
        this.distance = distance;
    }
}
