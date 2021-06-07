package nextstep.subway.line.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor
public class Line extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    //private List<Section> sections;

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void update(final Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
