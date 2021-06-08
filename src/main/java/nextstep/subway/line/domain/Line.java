package nextstep.subway.line.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_line_name", columnNames={"name"}))
public class Line extends BaseEntity {

    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public Line appendSection(Section section) {
        sections.add(section);
        return this;
    }

    public void update(final Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections();
    }
}
