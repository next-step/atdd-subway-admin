package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    Sections sections = new Sections();

    private int distance;

    public Line() {

    }

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line of(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.distance = lineRequest.getDistance();

        return this;
    }

    public Sections addSection(Section toSection) {
        return sections;
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

    public int getDistance() {
        return distance;
    }
}
