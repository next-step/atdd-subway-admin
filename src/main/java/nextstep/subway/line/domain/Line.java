package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {
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

    private void addSection(Section section) {
        this.sections.add(section);
        section.addLine(this);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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
        if (sections.getSections().size() <= 1) {
            throw new IllegalStateException("구간에서 역을 제거할 수 없습니다.");
        }

        Optional<Section> upLineStation = sections.getSections().stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();
        Optional<Section> downLineStation = sections.getSections().stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            Long newDistance = upLineStation.get().getDistance().get() + downLineStation.get().getDistance().get();
            Section newSection = new Section(this, downLineStation.get().getUpStation(), upLineStation.get().getDownStation(), newDistance);
            sections.getSections().add(newSection);
        }

        upLineStation.ifPresent(it -> sections.remove(it));
        downLineStation.ifPresent(it -> sections.remove(it));
    }
}
