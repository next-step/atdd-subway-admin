package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static nextstep.subway.exception.CannotAddSectionException.NO_MATCHED_STATION;

@Embeddable
public class LineStations {

    private static final int ZERO_DISTANCE = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStationList = new LinkedList<>();

    protected LineStations() {
    }

    public LineStations(LineStation ...lineStations) {
        lineStationList.addAll(Lists.newArrayList(lineStations));
    }

    public void addSection(Line line, Station upStation, Station downStation, Integer distance) {
        verifyDistanceMoreThanZero(distance);

        if (lineStationList.isEmpty()) {
            lineStationList.add(new LineStation(line, upStation, downStation, distance));
            return;
        }
        LineStation previousSection = getPreviousSection(upStation, downStation);
        List<LineStation> appendedSections = previousSection.addSection(upStation, downStation, distance);

        replaceSection(previousSection, appendedSections);
    }

    private LineStation getPreviousSection(Station upStation, Station downStation) {
        return lineStationList.stream()
                .filter(lineStation -> lineStation.canAddSection(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new CannotAddSectionException(NO_MATCHED_STATION));
    }

    private void replaceSection(LineStation previouSection, List<LineStation> appendedSections) {
        int previousLineStationIndex = lineStationList.indexOf(previouSection);
        lineStationList.remove(previousLineStationIndex);
        lineStationList.addAll(previousLineStationIndex, appendedSections);
    }

    public Stream<LineStation> stream() {
        return lineStationList.stream();
    }

    private void verifyDistanceMoreThanZero(Integer distance) {
        if (distance <= ZERO_DISTANCE) {
            throw new CannotAddSectionException(CannotAddSectionException.DISTANCE_LESS_THAN_ZERO);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStations that = (LineStations) o;
        return lineStationList.equals(that.lineStationList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStationList);
    }

    @Override
    public String toString() {
        return "LineStations{" +
                "lineStationList=" + lineStationList +
                '}';
    }
}
