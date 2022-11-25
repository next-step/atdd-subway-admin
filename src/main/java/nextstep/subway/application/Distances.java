package nextstep.subway.application;

import nextstep.subway.domain.ErrorMessage;

import java.util.List;

public class Distances {

    private List<Distance> distances;

    public Distances(List<Distance> distances) {
        this.distances = distances;
    }

    public int compareToAllDistance(Distance distance) {
        return Integer.compare(distances.stream().mapToInt(Distance::getDistance).sum(), distance.getDistance());
    }

    public void checkLessThanAllSectionDistance(Distance distance) {
        if(compareToAllDistance(distance) <= 0) {
            throw new IllegalArgumentException(ErrorMessage.EXCEED_ALL_SECTION_DISTANCE.getMessage());
        }
    }
}
