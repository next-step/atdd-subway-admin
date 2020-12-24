package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line")
@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "distance")
    private int distance;

    @Embedded
    private LastStation lastStation;

    @Embedded
    private Sections sections;

    private Line(final String name, final String color, final int distance, final LastStation lastStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.lastStation = lastStation;
    }

    public static Line of(final String name, final String color, final int distance, final LastStation lastStation) {
        Line line = new Line(name, color, distance, lastStation);
        line.initSection();
        return line;
    }

    private void initSection() {
        Section section = Section.of(this, lastStation.getUpStation(), lastStation.getDownStation(), distance);
        sections = Sections.init(section);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.distance = line.distance;
        this.lastStation = line.lastStation;
        this.sections = line.sections;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        Section section = Section.of(this, upStation, downStation, distance);
        sections.add(section);
    }
}
