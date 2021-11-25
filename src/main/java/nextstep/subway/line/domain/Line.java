package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    private static final String ERROR_MESSAGE = "빈 값을 입력하였습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        validate(name, color);
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getSections().stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStationResponses() {
        return getStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<SectionResponse> getSectionResponses() {
        return getSections().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    private void validate(String name, String color) {
        if (isBlank(name) || isBlank(color)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }
}
