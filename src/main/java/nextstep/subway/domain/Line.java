package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @Column
    private Long distance;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Long distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Line of(String name, String color, Long distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
    }

    public static Line of(LineRequest lineRequest, Station upStation, Station downStation) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance(), upStation, downStation);
    }

    public static Line of(LineUpdateRequest lineUpdateRequest, Long distance, Station upStation, Station downStation) {
        return new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor(), distance, upStation, downStation);
    }

    public void addSection(Station upStation, Station downStation, Long distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
        newSection.validateDistance();
    }

    public void update(Line newLine) {
        this.name = newLine.name;
        this.color = newLine.color;
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

    public Long getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    public boolean isSameUpStation(Station newUpStation) {
        return upStation.equals(newUpStation);
    }

    public boolean isSameDownStation(Station newDownStation) {
        return downStation.equals(newDownStation);
    }

    public boolean isSameOtherStation(Station newUpStation, Station newDownStation) {
        if (upStation.equals(newDownStation)) {
            return true;
        }

        if (downStation.equals(newUpStation)) {
            return true;
        }

        return false;
    }

    public boolean isSameAnyStation(Station newUpStation, Station newDownStation) {
        if (isSameUpStation(newUpStation)) {
            return true;
        }

        if (isSameDownStation(newDownStation)) {
            return true;
        }

        if (isSameOtherStation(newUpStation, newDownStation)) {
            return true;
        }

        return false;
    }
}
