package nextstep.subway.line.domain;

import static java.util.Collections.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.AlreadyInitializedException;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
        section.setLine(this);
    }

    private void validateSection(Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 인 구간을 추가할 수는 없습니다.");
        }

        if (sections.contains(section)) {
            throw new AlreadyInitializedException("이미 본 라인에 존재하는 구간입니다.");
        }

        if (section.isAddedToLine()) {
            throw new AlreadyInitializedException("이미 특정 라인에 포함되어있는 구간입니다.");
        }
    }
    // public List<Station> getStations() {
    //     List<Station> stations = new ArrayList<>();
    //     Station station = getStartStation();
    //
    //     while (!station.isLast()) {
    //         stations.add(station);
    //         station = station.nextStation();
    //     }
    //
    //     stations.add(station);
    //
    //     return unmodifiableList(stations);

    // }

    public List<Section> getSections() {
        return unmodifiableList(sections);
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
    // private Station getStartStation() {
    //     return sections.stream()
    //         .map(Section::getUpStation)
    //         .filter(Station::isFirst)
    //         .findAny()
    //         .orElseThrow(() -> new LineEndpointException("비 정상적인 노선입니다. 출발역이 존재하지 않습니다."));

    // }
}
