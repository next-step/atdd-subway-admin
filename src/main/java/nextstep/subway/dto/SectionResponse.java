package nextstep.subway.dto;

import java.util.List;
import java.util.Set;

public class SectionResponse {

    private List<Integer> distances;
    private Set<String> stationNames;

    public SectionResponse(List<Integer> distance, Set<String> stationNames) {
        this.distances = distance;
        this.stationNames = stationNames;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public Set<String> getStationNames() {
        return stationNames;
    }
}
