package nextstep.subway.line.domain;

import javax.persistence.*;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(LineRequest request, Station upStation, Station downStation) {
        this.name = request.getName();
        this.color = request.getColor();
        addSection(Section.getInstance(this, upStation, downStation, request.getDistance()));
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

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public List<Station> stations() {
        return sections.getStations();
    }

    public void updateNameColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    //TODO 계속해서 Line -> Sections로 넘어가는데 괜찮을까?
    public void checkSectionStations(Station upStation, Station downStation) {
        sections.checkSectionStations(upStation, downStation);
    }
}
