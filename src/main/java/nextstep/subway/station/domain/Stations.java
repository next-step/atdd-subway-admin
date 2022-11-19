package nextstep.subway.station.domain;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stations {

    private Map<Long, Station> stationMap = new HashMap<>();

    public Stations(List<Station> stations) {
        for (Station station : stations) {
            stationMap.put(station.getId(), station);
        }
    }

    public Station get(Long stationId) {
        if (this.stationMap.get(stationId) == null) {
            throw new EntityNotFoundException();
        }
        return this.stationMap.get(stationId);
    }
}
