package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DuplicateBothStationException;
import nextstep.subway.line.exception.NotMatchedStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.contains(section)) {
            return;
        }
        validateDuplicateBothStation(section);
        validateNotMatchedStation(section);
        insertion(section);
        sections.add(section);
    }

    private void validateDuplicateBothStation(Section section) {
        for (Section mySection : sections) {
            throwExceptionIfDuplicateBothStation(section, mySection);
        }
    }

    private void throwExceptionIfDuplicateBothStation(Section section, Section mySection) {
        if (mySection.isDuplicate(section)) {
            throw new DuplicateBothStationException(section.getUpStation(), section.getDownStation());
        }
    }

    private void validateNotMatchedStation(Section section) {
        if (sections.isEmpty()) {
            return;
        }
        for (Section mySection : sections) {
            if (mySection.anyMatched(section)) {
                return;
            }
        }
        throw new NotMatchedStationException(section.getUpStation(), section.getDownStation());
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }
        Section section = getHeadSection();
        return getOrderedStations(section);
    }

    private List<Station> getOrderedStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        while ((section = getNextSection(section)) != null) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public Section getHeadSection() {
        List<Station> upStations = new ArrayList<>();
        List<Station> downStations = new ArrayList<>();
        for (Section section : sections) {
            upStations.add(section.getUpStation());
            downStations.add(section.getDownStation());
        }
        upStations.removeAll(downStations);
        Station upStationOfHeadSection = upStations.get(0);
        return findByUpStation(upStationOfHeadSection)
                .orElseThrow(() -> {throw new StationNotFoundException(upStationOfHeadSection.getId());});
    }

    private Optional<Section> findByUpStation(Station station) {
        return sections.stream()
                .filter(s -> station.isSameName(s.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findByDownStation(Station station) {
        return sections.stream()
                .filter(s -> station.isSameName(s.getDownStation()))
                .findFirst();
    }

    public Section getNextSection(Section section) {
        Station downStation = section.getDownStation();
        return sections.stream()
                .filter(s -> downStation.isSameName(s.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    private void insertion(Section section) {
        findByUpStation(section.getUpStation())
                .ifPresent(find -> find.shiftBack(section));
        findByDownStation(section.getDownStation())
                .ifPresent(find -> find.shiftForward(section));
    }

    public List<Section> getSections() {
        return sections;
    }
}
