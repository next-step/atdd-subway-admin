package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

import static nextstep.subway.common.ErrorMessage.NOT_ALLOW_ADD_SECTION;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String color;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Sections sections = new Sections();

    @Embedded
    private Distance distance;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);

        Section section = new Section(upStation, downStation, new Distance(distance));
        this.sections.add(section);
        section.addLine(this);

        addLineStation(upStation, downStation);
    }

    private void addLineStation(Station upStation, Station downStation) {
        lineStations.add(LineStation.of(this, upStation));
        lineStations.add(LineStation.of(this, downStation));
    }

    public void addSection(Section section) {
        section.addLine(this);

        // 상행선 동일 -> 하행선 추가
        if (Objects.equals(upStation, section.getUpStation())) {
            sections.addBetweenSection(section);
            lineStations.add(LineStation.of(this, section.getDownStation()));
            return;
        }

        throw new IllegalArgumentException(NOT_ALLOW_ADD_SECTION.getMessage());
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Sections getSections() {
        return sections;
    }

    public Distance getDistance() {
        return distance;
    }
}
