package nextstep.subway.domain.station;

import nextstep.subway.message.StationMessage;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class StationName {

    @Column(unique = true)
    private String name;

    protected StationName() {

    }

    public StationName(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(String name) {
        if(!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(StationMessage.ERROR_STATION_NAME_SHOULD_BE_NOT_NULL.message());
        }
    }

    public String value() {
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
        return name != null ? name.hashCode() : 0;
    }
}
