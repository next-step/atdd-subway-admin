package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SectionSortingException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        if (sections.size() > 0) {
            checkValidation(section);
        }
        sections.add(section);
    }

    private void checkValidation(Section section) {
        boolean isUpStation = isExistUpStation(section.getUpStation());
        boolean isDownStation = isExistDownStation(section.getDownStation());

        if (isUpStation && isDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 등록되어 있습니다.");
        }

        if (!isUpStation && !isDownStation) {
            throw new IllegalArgumentException("상행역과 하행역 모두 존재하지 않습니다.");
        }
    }

    private boolean isExistDownStation(Station downStation) {
        return sections.stream()
            .anyMatch(section -> section.getDownStation() == downStation);
    }

    private boolean isExistUpStation(Station upStation) {
        return sections.stream()
            .anyMatch(section -> section.getUpStation() == upStation);
    }

    public List<Station> getSortedStations() {
        if (isStationsEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(findAllStations());
    }

    private boolean isStationsEmpty() {
        return sections.size() <= 0;
    }

    private List<Station> findAllStations() {
        Section firstSection = findFirstSection();
        List<Station> stations = new ArrayList<>(
            Arrays.asList(firstSection.getUpStation(), firstSection.getDownStation())
        );

        List<Station> upStations = upStationsOfSections();
        Section lastSection = firstSection;
        while (upStations.contains(lastSection.getDownStation())) {
            Station finalDownStation = lastSection.getDownStation();
            lastSection = sections.stream()
                .filter(section -> section.getUpStation() == finalDownStation)
                .findFirst()
                .orElseThrow(SectionSortingException::new);
            stations.add(lastSection.getDownStation());
        }
        return stations;
    }

    private Section findFirstSection() {
        List<Station> downStations = downStationsOfSections();
        Section firstSection = sections.get(0);
        while (downStations.contains(firstSection.getUpStation())) {
            Station finalUpStation = firstSection.getUpStation();
            firstSection = sections.stream()
                .filter(section -> section.getDownStation() == finalUpStation)
                .findFirst()
                .orElseThrow(SectionSortingException::new);
        }
        return firstSection;
    }

    private List<Station> upStationsOfSections() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> downStationsOfSections() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sections sections1 = (Sections)o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
