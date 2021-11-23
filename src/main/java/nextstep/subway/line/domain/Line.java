package nextstep.subway.line.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.common.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    /**
     * TODO :  1) 고아객체 설정에 대해 학습하기
     *         2) 일급컬렉션으로 관리하기
     */
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

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

    public void addSection(Section section) {
        //존재하는지
        sections.stream()
                .filter(it -> it.getStation().equals(section.getStation()))
                .findFirst()
                .ifPresent(it -> it.updateStation(section.getNextStation(), section.getDistance()));

        this.sections.add(section);
        section.addLine(this);
        System.out.println("firststation ::" + findFirstStation());
    }

    private List<Station> getStations() {
        return sections.stream().map(Section::getStation).collect(Collectors.toList());
    }

    private List<Station> getNextStations() {
        return sections.stream().map(Section::getNextStation).collect(Collectors.toList());
    }

    private Station findFirstStation() {
        return sections.stream()
                .filter(section -> !getNextStations().contains(section.getStation()))
                .findFirst()
                .orElseThrow(StationNotFoundException::new)
                .getStation();
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

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
