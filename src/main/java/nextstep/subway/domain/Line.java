package nextstep.subway.domain;

import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(final LineRequest lineRequest, final Section section) {
        this(
                lineRequest.getName(),
                lineRequest.getColor(),
                section
        );
    }

    public Line(final String name, final String color, final Section section) {
        this.name = name;
        this.color = color;
        this.sections.addSection(section);
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

    public void update(final LineRequest lineRequest) {
        Optional.ofNullable(lineRequest.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(lineRequest.getColor()).ifPresent(color -> this.color = color);
    }

    public Sections getSections() {
        return sections;
    }
}
