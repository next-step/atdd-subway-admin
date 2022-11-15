package nextstep.subway.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Stations {

    public static final Stations EMPTY = new Stations(Collections.emptyList());
    private final List<Station> stationList;

    public Stations(Station ...stations) {
        this(Arrays.stream(stations).collect(Collectors.toList()));
    }

    public Stations(List<Station> stationList) {
        this.stationList = stationList;
    }

    public List<Station> getStationList() {
        return Collections.unmodifiableList(stationList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stations stations = (Stations) o;
        return Objects.equals(stationList, stations.stationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationList);
    }
}
