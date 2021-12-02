package nextstep.subway.section.domain;

import nextstep.subway.line.exception.NotValidStationException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int START_INDEX = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addedSection) {
        List<Station> stations = getStations();
        validateStation(stations, addedSection);
        
        if (stations.contains(addedSection.getUpStation())) {
            findSectionByUpStation(addedSection.getUpStation()).ifPresent(foundSection ->
                    foundSection.update(addedSection.getDownStation(), foundSection.getDownStation(), foundSection.getDistance() - addedSection.getDistance()));
        }

        if (stations.contains(addedSection.getDownStation())) {
            findSectionByDownStation(addedSection.getDownStation()).ifPresent(foundSection ->
                    foundSection.update(foundSection.getUpStation(), addedSection.getUpStation(), foundSection.getDistance() - addedSection.getDistance()));
        }

        sections.add(addedSection);
    }

    private void validateStation(List<Station> stations, Section addedSection) {
        if ((stations.contains(addedSection.getUpStation()) && stations.contains(addedSection.getDownStation())) ||
                !stations.isEmpty() && !(stations.contains(addedSection.getUpStation()) && stations.contains(addedSection.getDownStation()))) {
            throw new NotValidStationException("구간 추가할 역이 잘못 입력됐습니다.");
        }
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public boolean isContainsSection(Section section) {
        return sections.contains(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return stations.stream().distinct().collect(Collectors.toList());
    }

    public List<Station> getSortedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }
        List<Station> stations = new LinkedList<>();

        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());
        stations.add(firstSection.getDownStation());
        Section nextSection = nextSection(firstSection);

        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = nextSection(nextSection);
        }

        return stations;
    }

    private Section nextSection(Section firstSection) {
        return sections.stream()
                .filter(firstSection::isNextSection)
                .findFirst()
                .orElse(null);
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    private Section findFirstSection() {
        Section section = sections.get(START_INDEX);
        Section foundFirstSection = null;

        while (section != null) {
            foundFirstSection = section;
            section = sections.stream()
                    .filter(section::isPrevSection)
                    .findFirst()
                    .orElse(null);
        }

        return foundFirstSection;
    }
}
