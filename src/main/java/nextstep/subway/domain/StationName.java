package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.util.StringUtils;

@Embeddable
public class StationName {

    private static final String INVALID_EMPTY_OR_NULL_STATION_NAME = "지하철 역 명이 비어있습니다.";

    @Column(unique = true)
    private String name;

    protected StationName() {}

    private StationName(String name) {
        this.name = name;
    }

    public static StationName from(String name) {
        validateStateName(name);
        return new StationName(name);
    }

    public String getName() {
        return this.name;
    }

    private static void validateStateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(INVALID_EMPTY_OR_NULL_STATION_NAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationName that = (StationName) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
