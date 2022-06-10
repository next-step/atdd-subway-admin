package nextstep.subway.domain.line;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.station.Station;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UK_LINE_NAME", columnNames = "name"))
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @OneToOne
    @JoinColumn(
        name = "up_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_LINE_UP_STATION")
    )
    private Station upStation;

    @OneToOne
    @JoinColumn(
        name = "down_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_LINE_DOWN_STATION")
    )
    private Station downStation;

    private int distance;

    protected Line() {
    }

    public Line(
        String name,
        String color,
        Station upStation,
        Station downStation,
        int distance
    ) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public void updateColor(String newName, String newColor) {
        this.name = newName;
        this.color = newColor;
    }
}
