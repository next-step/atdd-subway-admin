package nextstep.subway.station.domain;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.line.domain.LineName;

import javax.persistence.Column;

public class StationName {
    @Column(unique = true)
    String name;

    protected StationName() {
    }

    private StationName(String name) {
        validateStationName(name);
        this.name = name;
    }

    private void validateStationName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_STATIONNAME_EMPTY);
        }
    }

    public static StationName from(String name) {
        return new StationName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof  StationName)) {
            return false;
        }
        return ((StationName)obj).getName() == name;
    }
}
