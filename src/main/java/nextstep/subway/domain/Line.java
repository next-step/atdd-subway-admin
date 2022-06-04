package nextstep.subway.domain;

import static java.util.Arrays.asList;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    private long upStationId;

    private long downStationId;

    private long distance;

    protected Line() {
    }

    public Line(final String name, final String color, final long upStationId, final long downStationId,
                final long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(final String name, final String color) {
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getDistance() {
        return distance;
    }

    public List<Long> getStationsIds() {
        return asList(upStationId, downStationId);
    }

    public void update(final Line line) {
        this.name = line.name;
        this.color = line.color;
    }
}
