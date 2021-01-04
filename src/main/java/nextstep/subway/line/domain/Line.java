package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @ManyToMany
    @JoinTable(name = "station_line"
            , joinColumns = @JoinColumn(name = "line_id")
            , inverseJoinColumns = @JoinColumn(name = "station_id"))
    @JsonManagedReference
    private List<Station> stations = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = line.getStations();
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
        return this.stations;
    }

    private void addStations(Station station) {
        this.stations.add(station);
//        station.addLine(this);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSections(Station upwardStation, Station downStation, int distance) {
        this.addSections(new Section(this, upwardStation, downStation, new Distance(distance)));
    }

    public void addSections(Station upwardStation, Station downStation, Distance distance) {
        this.addSections(new Section(this, upwardStation, downStation, distance));
    }

    public void addSections(Section section) {
        this.sections.add(section);
        this.addStations(section.getUpStation());
        this.addStations(section.getDownStation());
        section.setLine(this);
    }

    /**
     * 노선에 포함 된 구간과 역을 상행 종점부터 하행 종점까지 순서대로 정렬합니다.
     */
    public void sort() {
        this.sections = this.sortSections();
        this.stations = this.sortStation();
    }

    /**
     * 노선의 구간을 상행 종점부터 하행 종점까지 순서대로 정렬합니다.
     */
    private List<Section> sortSections() {
        List<Section> sortedSections = new ArrayList<>();

        // set first upStation
        Long firstUpStationId = this.findFirstUpStationInSections();
        this.sections.stream().filter(section -> section.getUpStation().getId().equals(firstUpStationId))
                .findAny().ifPresent(sortedSections::add);

        // add Sections
        for (int i = 0; i < this.sections.size() - 1; i++) {
            Long downStationId = sortedSections.get(i).getDownStation().getId();
            this.sections.stream().filter(section -> section.getUpStation().getId().equals(downStationId))
                    .findAny().ifPresent(sortedSections::add);
        }

        return sortedSections;
    }

    /**
     * 노선의 구간에서 상행 종점역의 ID를 찾습니다.
     * @return 상행 종점역의 ID
     */
    private Long findFirstUpStationInSections() {
        Set<Long> upStationIds = this.sections.stream().map(section -> section.getUpStation().getId())
                .collect(Collectors.toSet());
        Set<Long> downStationIds = this.sections.stream().map(section -> section.getDownStation().getId())
                .collect(Collectors.toSet());
        upStationIds.removeAll(downStationIds);
        return upStationIds.stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("cannot found first up-station."));
    }

    /**
     * 정렬된 구간을 기준으로 역을 정렬합니다.
     * @return
     */
    private List<Station> sortStation() {
        List<Station> sortedStations = new ArrayList<>();
        int i = 0;
        for (i = 0; i < this.sections.size(); i++) {
            sortedStations.add(this.sections.get(i).getUpStation());
        }
        sortedStations.add(this.sections.get(i - 1).getDownStation());
        return sortedStations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
