package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.InvalidDataException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Embeddable
public class Sections {

    private static final int FIRST_INDEX = 0;
    private static final int PREVIOUS_INDEX_SIZE = 1;
    private static final int NOT_EXIST_INDEX = -1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    protected Sections() {
    }

    private Sections(Section section) {
        validateNotNull(section);
        this.list.add(section);
    }

    public static Sections from(Section section) {
        return new Sections(section);
    }

    List<Station> stations() {
        return new ArrayList<>(removedDuplicateStations());
    }

    void setLine(Line line) {
        for (Section section : list) {
            section.setLine(line);
        }
    }

    public void addSection(Section section) {
        validateNotNull(section);
        List<Station> stations = stations();
        int upStationIndex = stations.indexOf(section.upStation());
        int downStationIndex = stations.indexOf(section.downStation());
        validateIndexes(section, upStationIndex, downStationIndex);

        if (isFist(downStationIndex)) {
            list.add(FIRST_INDEX, section);
            return;
        }
        if (isLast(stations, upStationIndex)) {
            list.add(section);
            return;
        }
        if (isExistIndex(upStationIndex)) {
            list.get(upStationIndex).changeUpStation(section);
            list.add(upStationIndex, section);
            return;
        }
        list.get(downStationIndex - PREVIOUS_INDEX_SIZE).changeDownStation(section);
        list.add(downStationIndex - PREVIOUS_INDEX_SIZE, section);
    }

    private void validateNotNull(Section section) {
        Assert.notNull(section, "section must not be null");
    }

    private boolean isLast(List<Station> stations, int index) {
        return index == (stations.size() - PREVIOUS_INDEX_SIZE);
    }

    private void validateIndexes(Section section, int upStationIndex, int downStationIndex) {
        if (doesNotContainOne(upStationIndex, downStationIndex)) {
            throw new InvalidDataException(
                String.format(
                    "stations of section(%s) must be only one overlapping station", section));
        }
    }

    private boolean doesNotContainOne(int upStationIndex, int downStationIndex) {
        return isExistIndex(upStationIndex) == isExistIndex(downStationIndex);
    }

    private boolean isExistIndex(int index) {
        return index > NOT_EXIST_INDEX;
    }

    private boolean isFist(int index) {
        return index == FIRST_INDEX;
    }

    private Set<Station> removedDuplicateStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : list) {
            stations.addAll(section.stations());
        }
        return stations;
    }

    @Override
    public String toString() {
        return "Sections{" +
            "list=" + list +
            '}';
    }
}
