package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ServiceException;
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
    private final static int MIN_SECTION_COUNT = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        for (Section section : sections) {
            add(section);
        }
    }

    public void add(Section section) {
        if (sections.contains(section)) {
            return;
        }
        validateDuplicateBothStation(section);
        insertion(section);
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

    protected Section getHeadSection() {
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

    private void insertion(Section newSection) {
        findSectionByUpStationOrDownStation(newSection)
                .ifPresent(section -> section.shift(newSection));

        if (hasAnyMatchedStation(newSection)) {
            sections.add(newSection);
            return;
        }

        throw new NotMatchedStationException(newSection.getUpStation(), newSection.getDownStation());
    }

    private boolean hasAnyMatchedStation(Section section) {
        if (sections.isEmpty()) {
            return true;
        }
        return sections.stream()
                .anyMatch(mySection -> mySection.anyMatched(section));
    }

    private Optional<Section> findSectionByUpStationOrDownStation(Section section) {
        Optional<Section> upStation = findByUpStation(section.getUpStation());
        if (upStation.isPresent()) {
            return upStation;
        }
        return findByDownStation(section.getDownStation());
    }

    public List<Section> getSections() {
        return sections;
    }

    public void deleteSectionByStation(Station station) {
        validateDeleteSection(station);
        findByDownStation(station)
                .ifPresent(section -> {
                    Section nextSection = getNextSection(section);
                    mergeIntoNextSection(section, nextSection);
                    sections.remove(section);
                });
        findByUpStation(station)
                .ifPresent(section -> sections.remove(section));
    }

    private void validateDeleteSection(Station station) {
        if (sections.size() <= MIN_SECTION_COUNT) {
            throw new ServiceException("남은 구간은 삭제할 수 없습니다.");
        }

        Optional<Section> upStation = findByUpStation(station);
        (upStation.isPresent() ? upStation : findByDownStation(station))
                .orElseThrow(() -> {throw new StationNotFoundException(station.getId());});
    }

    private void mergeIntoNextSection(Section section, Section nextSection) {
        if (nextSection != null) {
            nextSection.merge(section);
        }
    }
}
