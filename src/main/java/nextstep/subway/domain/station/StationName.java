package nextstep.subway.domain.station;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class StationName {
    @Column(name = "name", unique = true)
    private String value;

    protected StationName() {
    }

    public static StationName of(String name) {
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("지하철명을 지정해주세요.");
        }

        return new StationName(name);
    }

    private StationName(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }
}
