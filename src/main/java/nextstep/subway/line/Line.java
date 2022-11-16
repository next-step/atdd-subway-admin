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

    protected Line() {
    }
}
