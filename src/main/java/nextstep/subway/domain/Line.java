package nextstep.subway.domain;

import nextstep.subway.exception.LineException;

import javax.persistence.*;

import static nextstep.subway.exception.LineExceptionMessage.EMPTY_LINE_NAME;
import static nextstep.subway.exception.LineExceptionMessage.EMPTY_STATION;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        validateLine(name, upStation, downStation);
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateLine(String name, Station upStation, Station downStation) {
        if (name == null || name.isEmpty()) {
            throw new LineException(EMPTY_LINE_NAME.getMessage());
        }
        if (upStation == null || downStation == null) {
            throw new LineException(EMPTY_STATION.getMessage());
        }
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

    public void modifyLine(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
