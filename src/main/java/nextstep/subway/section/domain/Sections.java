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

    public boolean isAddable(Station upStation, Station downStation, Long distance) {
        if (sections.isEmpty()) return true;

        return !containsBetweenDownStationAndDistance(downStation, distance)
                && !containsBetweenUpStationAndDistance(upStation, distance)
                && !containsStationsExactly(upStation, downStation)
                && containsStationAny(upStation, downStation);
    }

    private boolean containsBetweenDownStationAndDistance(Station downStation, Long distance) {
        return sections.stream()
                .filter(item -> item.isDownStationBetween(downStation, distance))
                .count() > 0L;
    }

    private boolean containsBetweenUpStationAndDistance(Station upStation, Long distance) {
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

        return sort(sections.get(0));
    }

    private List<Section> sort(Section of) {
        List<Section> sortedList = null;

        boolean findUpStation = false;
        for (Section section : sections) {
            if (of.isUpStationEqualsDownStationOf(section)) {
                findUpStation = true;
                sortedList = sort(section);
                sortedList.add(of);
            }
        }

        if (!findUpStation) {
            sortedList = new ArrayList<>();
            sortedList.add(of);

            return sortedList;
        }

        return sortedList;
    }
}
