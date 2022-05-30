package nextstep.subway.dto;

import nextstep.subway.domain.Station;

import java.util.Objects;

public class StationRequest {
    private final String name;

    private StationRequest() {
        this.name = null;
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }

    @Override
    public String toString() {
        return "StationRequest{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StationRequest that = (StationRequest) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
