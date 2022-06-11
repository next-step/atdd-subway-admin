package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private final static int LAST_SIZE = 1;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();
    @ManyToOne
    @JoinColumn(nullable = false)
    private Station ascend;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Station descend;

    protected Sections() {
    }

    public Sections(Line line, int distance, Station upStation, Station downStation) {
        checkSame(upStation, downStation);

        Section section = new Section(line, distance, upStation, downStation);
        this.ascend = upStation;
        this.descend = downStation;
        this.sections.add(section);
    }

    public List<Station> getAllDistinctStationsOrderByAscending() {
        List<Station> sections = new ArrayList<>();
        Optional<Section> optionalSection = findByUpStation(ascend);
        while (optionalSection.isPresent()) {
            Section section = optionalSection.get();
            sections.add(section.getUpStation());
            sections.add(section.getDownStation());
            optionalSection = findByUpStation(section.getDownStation());
        }
        return sections.stream().distinct().collect(Collectors.toList());
    }

    private Optional<Section> findByUpStation(Station station) {
        return this.sections.stream().filter(section -> section.isUpStationEquals(station)).findFirst();
    }

    public void add(Line line, int distance, Station upStation, Station downStation) {
        checkSame(upStation, downStation);
        checkStationIncluded(upStation, downStation);

        updateEndStation(upStation, downStation);
        Section section = new Section(line, distance, upStation, downStation);
        checkAlreadyAdded(section);
        findIncludingSectionBy(upStation, downStation).ifPresent(value -> value.connect(section));
        this.sections.add(section);
    }

    private void updateEndStation(Station upStation, Station downStation) {
        if (Objects.equals(downStation, ascend)) {
            this.ascend = upStation;
        }

        if (Objects.equals(upStation, descend)) {
            this.descend = downStation;
        }
    }

    private Optional<Section> findIncludingSectionBy(Station upStation, Station downStation) {
        return this.sections.stream()
                .filter(section -> section.isUpStationEquals(upStation) || section.isDownStationEquals(downStation))
                .findAny();
    }

    private void checkSame(Station previousStation, Station nextStation) {
        if (Objects.equals(previousStation, nextStation)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkStationIncluded(Station upStation, Station downStation) {
        if (getAllDistinctStationsOrderByAscending().stream().noneMatch(station ->
                Objects.equals(station, upStation) || Objects.equals(station, downStation))) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나는 기존 구간 내 반드시 존재해야 합니다.");
        }
    }

    private void checkAlreadyAdded(Section section) {
        if (this.sections.contains(section)) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private void checkStationIncluded(Station station) {
        if (getAllDistinctStationsOrderByAscending().stream().noneMatch(distinctStation -> Objects.equals(distinctStation, station))) {
            throw new IllegalArgumentException("구간 내 존재하지 않는 역입니다.");
        }
    }

    private void checkLastSection() {
        if (Objects.equals(this.sections.size(), LAST_SIZE)) {
            throw new IllegalArgumentException("마지막 구간은 제거할 수 없습니다.");
        }
    }

    private Section findIncludingUpStationSectionBy(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.isUpStationEquals(upStation))
                .findAny().orElse(null);
    }

    private Section findIncludingDownStationSectionBy(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.isDownStationEquals(downStation))
                .findAny().orElse(null);
    }

    public void delete(Station station) {
        checkStationIncluded(station);
        checkLastSection();

        Section upSection = findIncludingUpStationSectionBy(station);
        Section downSection = findIncludingDownStationSectionBy(station);

        if (Objects.isNull(upSection)) {
            this.sections.remove(downSection);
            this.descend = downSection.getUpStation();
            return;
        }

        if (Objects.isNull(downSection)) {
            this.sections.remove(upSection);
            this.ascend = upSection.getDownStation();
            return;
        }

        downSection.updateDownStation(upSection.getDownStation());
        downSection.updateDistance(downSection.getDistance() + upSection.getDistance());
        this.sections.remove(upSection);
    }
}
