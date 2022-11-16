package nextstep.subway.line;

import nextstep.subway.common.vo.Color;
import nextstep.subway.common.vo.Name;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private Color color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(long id, Name name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
