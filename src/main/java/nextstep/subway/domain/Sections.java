package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();
    @ManyToOne
    @JoinColumn(nullable = false)
    private Station ascend;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Station descend;

    protected Sections() {
    }

    public Sections(Line line, Integer distance, Station upStation, Station downStation) {
        checkSame(upStation, downStation);
        checkDistance(distance);

        Section section = new Section(line, distance, upStation, downStation);
        this.ascend = upStation;
        this.descend = downStation;
        this.sections.add(section);
    }

    public List<Station> getAllDistinctStations() {
        List<Station> sections = new ArrayList<>();
        Section section = findByUpStationOrNull(ascend);
        while (section != null) {
            sections.add(section.getUpStation());
            sections.add(section.getDownStation());
            section = findByUpStationOrNull(section.getDownStation());
        }
        return sections.stream().distinct().collect(Collectors.toList());
    }

    private Section findByUpStationOrNull(Station station) {
        return this.sections.stream().filter(it -> Objects.equals(it.getUpStation(), station)).findFirst().orElse(null);
    }

    public void addSection(Line line, int distance, Station upStation, Station downStation) {
        checkSame(upStation, downStation);
        checkDistance(distance);
        checkAlreadyAdded(upStation, downStation);
        checkStationIncluded(upStation, downStation);

        Section section = new Section(line, distance, upStation, downStation);
        findIncludingSectionBy(upStation, downStation).ifPresent(value -> value.connect(section));

        if (downStation == ascend) {
            this.ascend = upStation;
        }

        if (upStation == descend) {
            this.descend = downStation;
        }

        this.sections.add(section);
    }

    private Optional<Section> findIncludingSectionBy(Station upStation, Station downStation) {
        return this.sections.stream()
                .filter(section -> Objects.equals(section.getUpStation(), upStation)
                        || Objects.equals(section.getDownStation(), downStation))
                .findFirst();
    }

    private void checkDistance(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리값은 1 이상 되어야 합니다.");
        }
    }

    private void checkSame(Station previousStation, Station nextStation) {
        if (Objects.equals(previousStation, nextStation)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkStationIncluded(Station upStation, Station downStation) {
        if (getAllDistinctStations().stream().noneMatch(station ->
                Objects.equals(station, upStation) || Objects.equals(station, downStation))) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나는 기존 구간 내 반드시 존재해야 합니다.");
        }
    }

    private void checkAlreadyAdded(Station upStation, Station downStation) {
        if (this.sections.stream().anyMatch(
                section -> Objects.equals(section.getUpStation(), upStation)
                        && Objects.equals(section.getDownStation(), downStation))) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }
}
