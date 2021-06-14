package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import nextstep.subway.line.dto.LineRequest;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line getUpdatedLineBy(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();

        return this;
    }
}
