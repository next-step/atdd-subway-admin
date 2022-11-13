package nextstep.subway.station.domain;

import nextstep.subway.common.util.SubwayValidator;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class StationName {
    @Column(unique = true, nullable = false)
    private String name;

    protected StationName() {
    }

    private StationName(String name) {
        this.name = name;
    }

    public static StationName from(String name) {
        SubwayValidator.validateNotNullAndNotEmpty(name);
        return new StationName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationName that = (StationName) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
