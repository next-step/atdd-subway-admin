package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = Sections.of(new ArrayList<>());

    public Line() { }

    public Line(String name, String color, Sections sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section){
        this.sections.getValues().add(section);
        section.toLine(this);
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

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return this.sections.getValues().stream()
                .map(section -> Stream.of(section.getDownStation(), section.getUpStation()))
                .flatMap(Stream::distinct).collect(Collectors.toList());
    }

    public void update(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        List<Section> values = this.sections.getValues();
        values.clear();
        values.add(section);
    }
}
