package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private List<Station> stations = new ArrayList<>();

    public Stations(List<Station> stations) {
        this.stations = new ArrayList<>(stations);
    }


}
