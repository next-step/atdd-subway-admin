package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL})
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation);
        updateUpStationOfSectionIfExists(upStation, downStation, distance);
        updateDownStationOfSectionIfExists(upStation, downStation, distance);
        sections.add(new Section(line, upStation, downStation, distance));
    }

    private void validateSection(Station upStation, Station downStation) {
        if (isPresentStation(upStation) && isPresentStation(downStation)) {
            throw new IllegalArgumentException("등록된 구간입니다.");
        }

        if (!sections.isEmpty() && !isPresentStation(upStation) && !isPresentStation(downStation)) {
            throw new IllegalArgumentException("등록할 수 있는 구간이 없습니다.");
        }
    }

    private void updateUpStationOfSectionIfExists(Station upStation, Station downStation, int distance) {
        if (!isPresentStation(upStation)) {
            return;
        }
        sections.stream()
                .filter(s -> s.equalUpStation(upStation))
                .findFirst()
                .ifPresent(s -> s.updateUpStation(downStation, distance));
    }

    private void updateDownStationOfSectionIfExists(Station upStation, Station downStation, int distance) {
        if (!isPresentStation(downStation)) {
            return;
        }
        sections.stream()
                .filter(s -> s.equalDownStation(downStation))
                .findFirst()
                .ifPresent(s -> s.updateDownStation(upStation, distance));
    }

    private boolean isPresentStation(Station station) {
        return stations().stream()
                .anyMatch(s -> s.equals(station));
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        Station station = firstStation();
        stations.add(station);

        while (isPresentNextSection(station)) {
            station = nextSection(station).getDownStation();
            stations.add(station);
        }

        return stations;
    }

    private Station firstStation() {
        Station upStation = sections.get(0).getUpStation();
        while (isPresentPreSection(upStation)) {
            upStation = preSection(upStation).getUpStation();
        }

        return upStation;
    }

    private boolean isPresentPreSection(Station station) {
        return sections.stream()
                .filter(s -> s.getDownStation() != null)
                .anyMatch(s -> s.equalDownStation(station));
    }

    private Section preSection(Station station) {
        return sections.stream()
                .filter(s -> s.equalDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 존재하지 않습니다."));
    }

    private boolean isPresentNextSection(Station station) {
        return sections.stream()
                .filter(s -> s.getUpStation() != null)
                .anyMatch(s -> s.equalUpStation(station));
    }

    private Section nextSection(Station station) {
        return sections.stream()
                .filter(s -> s.equalUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections(), sections1.sections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections());
    }
}
