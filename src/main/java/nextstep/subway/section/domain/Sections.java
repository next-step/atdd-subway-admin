package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections.addAll(sections);
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.addAll(
                    section.getStations()
            );
        }

        return stations;
    }

    public <R1> Stream<R1> mapOrderByUpStationToDownStation(Function<? super Section, ? extends R1> mapper) {
        List<Section> sorted = sort();
        return sorted.stream()
                .map(mapper);
    }

    public void addAndResizeDistanceBy(Section section) {
        resizeNearSection(section);

        sections.add(section);
    }

    public boolean isAddable(Station upStation, Station downStation, Distance distance) {
        if (sections.isEmpty()) {
            return true;
        }

        return !containsBetweenDownStationAndDistance(downStation, distance)
                && !containsBetweenUpStationAndDistance(upStation, distance)
                && !containsStationsExactly(upStation, downStation)
                && containsStationAny(upStation, downStation);
    }

    private void resizeNearSection(Section section) {
        sections.stream()
                .filter(item -> item.isSameUpStation(section) || item.isSameDownStation(section))
                .findFirst()
                .ifPresent((near) -> near.resizeAndChangeNearStation(section));
    }

    private boolean containsBetweenDownStationAndDistance(Station downStation, Distance distance) {
        return sections.stream()
                .filter(item -> item.isDownStationBetween(downStation, distance))
                .count() > 0L;
    }

    private boolean containsBetweenUpStationAndDistance(Station upStation, Distance distance) {
        return sections.stream()
                .filter(item -> item.isUpStationBetween(upStation, distance))
                .count() > 0L;
    }

    private boolean containsStationsExactly(Station upStation, Station downStation) {
        return containsStation(upStation) &&
                containsStation(downStation);
    }

    private boolean containsStationAny(Station upStation, Station downStation) {
        return containsStation(upStation) ||
                containsStation(downStation);
    }

    private boolean containsStation(Station station) {
        return sections.stream()
                .filter(item -> item.isContains(station))
                .count() > 0L;
    }

    private List<Section> sort() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        Section topSection = getTopSection(sections.get(0));

        return sort(topSection);
    }

    private Section getTopSection(Section firstSection) {
        for (Section section : sections) {
            if (firstSection == section) continue;

            if (firstSection.isLower(section)) {
                return getTopSection(section);
            }
        }

        return firstSection;
    }

    private List<Section> sort(Section upperSection) {
        List<Section> sortedList = new ArrayList<>();
        sortedList.add(upperSection);

        Section bottomSection = findBottomSection(upperSection);

        if (bottomSection == null) {
            return sortedList;
        }

        sortedList.addAll(sort(bottomSection));
        return sortedList;
    }

    private Section findBottomSection(Section of) {
        for (Section section : sections) {
            if (of.isUpper(section)) {
                return section;
            }
        }

        return null;
    }
}
