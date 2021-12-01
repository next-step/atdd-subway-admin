package nextstep.subway.line.domain;

import nextstep.subway.Exception.CannotUpdateSectionException;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station station = findFirstStation();
        stations.add(station);

        while (isExistNextSection(station)) {
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    private Station findFirstStation() {
        Station station = sections.get(0).getUpStation();
        while (isExistPreSection(station)) {
            Section section = findPreSection(station);
            station = section.getUpStation();
        }
        return station;
    }

    private boolean isExistPreSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(station));
    }

    private Section findPreSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 구간이 없습니다."));
    }

    private boolean isExistNextSection(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    private Section findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 없습니다."));
    }

    public void addLineSection(Station upStation, Station downStation, int distance) {
        boolean existUpStation = getStations().stream().anyMatch(it -> it.equals(upStation));
        boolean existDownStation = getStations().stream().anyMatch(it -> it.equals(downStation));

        validStation(existUpStation, existDownStation);
        if (existUpStation) {
            updateUpStation(upStation, downStation, distance);
        }
        if (existDownStation) {
            updateDownStation(upStation, downStation, distance);
        }
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalDownStation(downStation))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(upStation, distance));
    }

    private void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(it -> it.equalUpStation(upStation))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(downStation, distance));
    }

    private void validStation(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new CannotUpdateSectionException("상행역과 하행역이 이미 노선에 모두 등록되어있어서 추가할 수 없습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new CannotUpdateSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않아서 추가할 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
