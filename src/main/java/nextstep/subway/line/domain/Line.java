package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.global.domain.BaseEntity;
import nextstep.subway.global.exception.BadRequestException;
import nextstep.subway.global.exception.ExceptionType;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {

    }

    private Line(
        String name,
        String color,
        Long distance,
        Station upStation,
        Station downStation
    ) {
        validateLine(name, color);
        this.name = name;
        this.color = color;
        Section section = Section.of(upStation, downStation, distance);
        section.addLine(this);
    }

    private void validateLine(String name, String color) {
        validateEmptyName(name);
        validateEmptyColor(color);
    }

    private void validateEmptyName(String name) {
        if (Objects.isNull(name)) {
            throw new BadRequestException(ExceptionType.IS_EMPTY_LINE_NAME);
        }
    }

    private void validateEmptyColor(String color) {
        if (Objects.isNull(color)) {
            throw new BadRequestException(ExceptionType.IS_EMPTY_LINE_COLOR);
        }
    }

    public static Line of(
        String name,
        String color,
        Long distance,
        Station upStation,
        Station downStation
    ) {
        return new Line(
            name,
            color,
            distance,
            upStation,
            downStation
        );
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section section = this.sections.getFirstSection();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        Optional<Section> optionalNextSection = this.sections.getNextSection(section);
        while (optionalNextSection.isPresent()) {
            Section nextSection = optionalNextSection.get();
            stations.add(nextSection.getDownStation());
            optionalNextSection = this.sections.getNextSection(nextSection);
        }

        return stations;
    }

    public void registerSection(Section section) {
        section.addLine(this);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }
}
