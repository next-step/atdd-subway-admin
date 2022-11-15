package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {}

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validate(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validate(Section section) {
        validateHasBothStations(section);
        validateHasNotBothStations(section);
    }

    private void validateHasBothStations(Section section) {
        if (getStations().containsAll(section.findStations())) {
            throw new IllegalArgumentException("추가하려는 역이 모두 존재합니다.");
        }
    }

    private void validateHasNotBothStations(Section section) {
        if (hasNotBothStations(section)) {
            throw new IllegalArgumentException("상행성 하행선 모두 존재하지 않습니다.");
        }
    }

    private boolean hasNotBothStations(Section section) {
        List<Station> stations = getStations();
        return section.findStations()
                .stream()
                .noneMatch(stations::contains);
    }

    public void delete(Station station) {
        Optional<Section> upStation = sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst();

        Optional<Section> downStation = sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst();

        if (upStation.isPresent() && downStation.isPresent()) {
            // 추후 처리
        }

        deleteEndSection(upStation, downStation);
    }

    private void deleteEndSection(Optional<Section> upStation, Optional<Section> downStation) {
        upStation.ifPresent(sections::remove);
        downStation.ifPresent(sections::remove);
    }
}
