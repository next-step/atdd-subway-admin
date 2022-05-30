package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void initSection(Section section) {
        this.sections.add(section);
    }

    public void addSection(Section section) {
        validateSection(section);
        this.sections.forEach(it -> it.update(section));
        this.sections.add(section);
    }

    private void validateSection(Section section) {
        Station upStation = section.upStation();
        Station downStation = section.downStation();

        if (hasAlreadyBothStations(upStation, downStation)) {
            throw new IllegalArgumentException("상행, 하행역 모두 존재하는 노선입니다.");
        }
        if (hasNothingBothStations(upStation, downStation)) {
            throw new IllegalArgumentException("상행, 하행역 모두 존재하지 않는 노선입니다.");
        }
    }

    public List<StationResponse> toInOrderStationResponse() {
        return getInOrderStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public Set<Station> getInOrderStations() {
        return getInOrderSections().stream()
                .map(Section::stations)
                .flatMap(List::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<Section> getInOrderSections() {
        List<Section> inOrderStation = new ArrayList<>();
        Section section = lineFirstSection();

        while (section != null) {
            inOrderStation.add(section);
            section = findNextSection(section);
        }
        return inOrderStation;
    }

    private Section lineFirstSection() {
        if (isEmpty()) {
            return null;
        }
        Section section = this.sections.get(size() - 1);
        while (findPreviousSection(section) != null) {
            section = findPreviousSection(section);
        }
        return section;
    }

    private Section findPreviousSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameDownStation(section.upStation()))
                .findFirst()
                .orElse(null);
    }

    private Section findNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameUpStation(section.downStation()))
                .findFirst()
                .orElse(null);
    }

    private boolean hasNothingBothStations(Station upStation, Station downStation) {
        Set<Station> stations = getInOrderStations();
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    private boolean hasAlreadyBothStations(Station upStation, Station downStation) {
        Set<Station> stations = getInOrderStations();
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public int size() {
        return this.sections.size();
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
