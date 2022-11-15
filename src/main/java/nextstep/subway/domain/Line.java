package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.*;

@Entity
@Builder
@AllArgsConstructor
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(upStation, downStation));
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
        List<Station> allStations = new ArrayList<>();
        sections.getSections().forEach(section ->  {
            allStations.add(section.getUpStation());
            allStations.add(section.getDownStation());
        });

        // 중복제거
        return allStations.stream().distinct().collect(Collectors.toList());
    }

    public void update(LineRequest updateRequest) {
        if(!updateRequest.getName().isEmpty() && updateRequest.getName() != "") {
            this.name = updateRequest.getName();
        }
        if(!updateRequest.getColor().isEmpty() && updateRequest.getColor() != ""){
            this.color = updateRequest.getColor();
        }
    }

    public void addSection(Section newSection) {
        Objects.requireNonNull(newSection, NOT_FOUND_SECTION_ERR);
        sections.addSection(newSection);
    }


    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }


}
