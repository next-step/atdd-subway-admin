package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public Station getUpStation() {
        return sections.get(0).getUpStation();
    }

    public boolean isExistPreSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(station));
    }

    public Section findPreSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 없습니다."));
    }

    public boolean isExistNextSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    public Section findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 없습니다."));
    }

    public void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalDownStation(downStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(upStation, distance));
    }

    public void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalUpStation(upStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(downStation, distance));
    }

    public List<Section> getSections() {
        return this.sections;
    }
}
