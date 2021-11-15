package nextstep.subway.station.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.util.StringUtils;

@Embeddable
public class StationName {
    private static final String EMPTY_STATION_NAME_ERROR_MESSAGE = "역 이름이 비어있습니다. name=%s";

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    protected StationName() {}

    private StationName(String name) {
        this.name = name;
    }

    public static StationName from(String name) {
        validateStationName(name);
        return new StationName(name);
    }

    public String getValue() {
        return name;
    }

    private static void validateStationName(String name) {
        if (!StringUtils.hasLength(name)) {
            throw new IllegalArgumentException(String.format(EMPTY_STATION_NAME_ERROR_MESSAGE, name));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StationName that = (StationName)o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
