package nextstep.subway.line.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

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
        validateLine(name, color, distance);
        this.name = name;
        this.color = color;
        Section section = Section.of(upStation, downStation, distance);
        section.addLine(this);
    }

    private void validateLine(String name, String color, Long distance) {
        if (Objects.isNull(name)) {
            throw new BadRequestException(ExceptionType.IS_EMPTY_LINE_NAME);
        }

        if (Objects.isNull(color)) {
            throw new BadRequestException(ExceptionType.IS_EMPTY_LINE_COLOR);
        }

        if (Objects.isNull(distance) || distance < 1) {
            throw new BadRequestException(ExceptionType.IS_EMPTY_LINE_DISTANCE);
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

    public Sections getSections() {
        return sections;
    }

    public Set<StationResponse> getStationResponses() {
        Set<StationResponse> stations = new HashSet<>();
        for (Section section : sections.getItems()) {
            stations.add(StationResponse.of(section.getUpStation()));
            stations.add(StationResponse.of(section.getDownStation()));
        }

        return stations;
    }

    public List<SectionResponse> getSectionResponses() {
        return this.sections.getItems().stream()
            .map(SectionResponse::of)
            .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        section.addLine(this);
    }
}
