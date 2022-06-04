package nextstep.subway.domain;

import static nextstep.subway.domain.ErrorMessage.LINE_COLOR_EMPTY;
import static nextstep.subway.domain.ErrorMessage.LINE_NAME_EMPTY;
import static nextstep.subway.domain.ErrorMessage.UP_STATION_DOWN_STATION_SAME;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

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
    private Distance distance;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Distance distance) {
        this(null, name, color, null, null, distance);
    }

    public Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        this(null, name, color, upStation, downStation, distance);
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation, Distance distance) {
        validate(name, color, upStation, downStation);
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        sections = Sections.of(upStation, downStation, distance);
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station sectionUpStation, Station sectionDownStation, Distance distance) {
        Section section = new Section(sectionUpStation, sectionDownStation, distance);
        sections.add(section);
    }

    public void removeSection(Station station) {
        sections.remove(station);
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
        return sections.getStations();
    }

    public Station getStationByStationId(Long stationId) {
        return sections.getStations().stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private void validate(String name, String color, Station upStation, Station downStation) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(LINE_NAME_EMPTY.toString());
        }
        if (StringUtils.isEmpty(color)) {
            throw new IllegalArgumentException(LINE_COLOR_EMPTY.toString());
        }
        validateStation(upStation, downStation);
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(UP_STATION_DOWN_STATION_SAME.toString());
        }
    }
}
