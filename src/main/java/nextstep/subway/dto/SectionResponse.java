package nextstep.subway.dto;

import java.util.List;
import java.util.Set;

public class SectionResponse {

    private List<Integer> distances;
    private Set<String> sortNos;

    public SectionResponse(List<Integer> distance, Set<String> sortNo) {
        this.distances = distance;
        this.sortNos = sortNo;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public Set<String> getSortNos() {
        return sortNos;
    }
}
