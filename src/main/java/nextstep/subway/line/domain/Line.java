package nextstep.subway.line.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.line.dto.LineResponse;

import javax.persistence.*;

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

    public LineResponse toDto() {
        return LineResponse.of(id, name, color, getCreatedDate(), getModifiedDate());
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

}
