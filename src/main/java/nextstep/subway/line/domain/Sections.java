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
        validateFoundIndexes(section, upStationIndex, downStationIndex);
        addSectionByFoundIndexes(section, upStationIndex, downStationIndex);
    }

    private void addSectionByFoundIndexes(
        Section section, int upStationIndex, int downStationIndex) {
        if (isExistIndex(upStationIndex)) {
            addSectionByUpStationIndex(section, upStationIndex);
            return;
        }
        addSectionByDownStationIndex(section, downStationIndex);
    }

    private void addSectionByUpStationIndex(Section section, int index) {
        if (isLastIndex(index)) {
            list.add(section);
            return;
        }
        list.get(index).remove(section);
        list.add(index, section);
    }

    private void addSectionByDownStationIndex(Section section, int index) {
        if (isFistIndex(index)) {
            list.add(FIRST_INDEX, section);
            return;
        }
        list.get(index - PREVIOUS_INDEX_SIZE).remove(section);
        list.add(index, section);
    }

    private void validateNotNull(Section section) {
        Assert.notNull(section, "section must not be null");
    }

    private boolean isLastIndex(int index) {
        return index == lastStationIndex();
    }

    private int lastStationIndex() {
        return list.size();
    }

    private void validateFoundIndexes(Section section, int upStationIndex, int downStationIndex) {
        if (doesNotContainOnlyOneStation(upStationIndex, downStationIndex)) {
            throw new InvalidDataException(
                String.format(
                    "stations of section(%s) must be only one overlapping station", section));
        }
    }

    private boolean doesNotContainOnlyOneStation(int upStationIndex, int downStationIndex) {
        return isExistIndex(upStationIndex) == isExistIndex(downStationIndex);
    }

    private boolean isExistIndex(int index) {
        return index > NOT_EXIST_INDEX;
    }

    private boolean isFistIndex(int index) {
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
