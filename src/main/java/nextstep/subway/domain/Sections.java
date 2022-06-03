package nextstep.subway.domain;

import static nextstep.subway.domain.Section.mergeTwoSection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.UniqueSectionException;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
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

    public void deleteSection(Station station) {
        validateSizeOnlyOne();
        final Optional<Section> upStation = findSameUpStation(station);
        final Optional<Section> downStation = findSameDownStation(station);

        if (isMiddleSection(upStation, downStation)) {
            this.sections.add(mergeTwoSection(upStation.get(), downStation.get()));
        }
        upStation.ifPresent(sections::remove);
        downStation.ifPresent(sections::remove);
    }

    private boolean isMiddleSection(Optional<Section> upStation, Optional<Section> downStation) {
        return upStation.isPresent() && downStation.isPresent();
    }

    private void validateSizeOnlyOne() {
        if (size() == 1) {
            throw new UniqueSectionException("해당 노선의 유일한 구간이라 지울 수 없습니다.");
        }
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
        return findFirstSection(this.sections.get(size() - 1));
    }

    private Section findFirstSection(Section section) {
        Optional<Section> optionalSection = findSameDownStation(section.upStation());
        return optionalSection.map(this::findFirstSection).orElse(section);
    }

    private Section findNextSection(Section section) {
        Optional<Section> optionalSection = findSameUpStation(section.downStation());
        return optionalSection.orElse(null);
    }

    private Optional<Section> findSameUpStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.isSameUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSameDownStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.isSameDownStation(station))
                .findFirst();
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

    public boolean contains(Station station) {
        return this.sections.stream()
                .map(Section::stations)
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .contains(station);
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
