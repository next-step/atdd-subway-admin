package nextstep.subway.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "name", unique = true, nullable = false))
    private Name name;

    @Embedded
    @AttributeOverride(name = "color", column = @Column(name = "color", nullable = false, unique = true))
    private Color color;

    @Column(name = "up_station_id")
    private Long upStationId;

    @Column(name = "down_station_id")
    private Long downStationId;

    @Embedded
    @AttributeOverride(name = "distance", column = @Column(name = "distance"))
    private Distance distance;

    public Line(Name name, Color color, Long upStationId, Long downStationId, Distance distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    protected Line() {
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line updateName(Name name) {
        this.name = name;
        return this;
    }

    public Line updateColor(Color color) {
        this.color = color;
        return this;
    }

    public List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }
}
