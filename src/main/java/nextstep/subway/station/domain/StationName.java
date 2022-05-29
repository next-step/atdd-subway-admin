package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class StationName {
    private static final String STATION_NAME_NOT_NULL = "지하철역 이름은 빈값일 수 없습니다.";

    @Column(unique = true, nullable = false)
    private String name;

    protected StationName() {}

    private StationName(String name) {
        this.name = name;
    }

    public static StationName from(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(STATION_NAME_NOT_NULL);
        }
        return new StationName(name);
    }

    public String get() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
