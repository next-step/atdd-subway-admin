package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {
    public static final int SECTION_SIZE = 1;
    public static final int STATIONS_SIZE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        addSection(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    private void addSection(Section section) {
        this.sections.add(section);
        section.addLine(this);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Section addSection(Station upStation, Station downStation, Long distance) {
        Section section = new Section(this, upStation, downStation, distance);
        addSection(section);
        return section;
    }

    public void removeStationInSection(Station station) {
        removeStation(findPossibleStation(station));
    }

    private Station findPossibleStation(Station station) {
        if (sections.getSections().size() <= SECTION_SIZE) {
            throw new IllegalStateException("구간에서 역을 제거할 수 없습니다.");
        }

        return sections.getStations().stream()
                .filter(it -> it == station)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("제거 할 역이 노선 구간에 없습니다."));
    }

    private void removeStation(Station removePossibleStation) {

        // 개선 전
        Optional<Section> upLineStation = getUpLineStation(removePossibleStation);
        Optional<Section> downLineStation = getDownLineStation(removePossibleStation);

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            addNewSection(upLineStation.get(), downLineStation.get());
        }

        upLineStation.ifPresent(it -> sections.remove(it));
        downLineStation.ifPresent(it -> sections.remove(it));
    }

    private void addNewSection(Section upLineStation, Section downLineStation) {
        Long newDistance = upLineStation.getDistance().addDistanceToLong(downLineStation.getDistance());
        sections.getSections().add(new Section(this, downLineStation.getUpStation(), upLineStation.getDownStation(), newDistance));
    }

    private Optional<Section> getDownLineStation(Station removePossibleStation) {
        return sections.getSections().stream()
                .filter(it -> it.getDownStation() == removePossibleStation)
                .findFirst();
    }

    private Optional<Section> getUpLineStation(Station removePossibleStation) {
        return sections.getSections().stream()
                .filter(it -> it.getUpStation() == removePossibleStation)
                .findFirst();
    }
}
