package nextstep.subway.domain;

import static nextstep.subway.domain.ErrorMessage.LINE_COLOR_EMPTY;
import static nextstep.subway.domain.ErrorMessage.LINE_NAME_EMPTY;
import static nextstep.subway.domain.ErrorMessage.UP_STATION_DOWN_STATION_SAME;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

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
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateNameAndColor(String name, String color) {
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
        stations.add(upStation);
        stations.add(downStation);
        return stations;
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
