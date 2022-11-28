package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.util.StringUtils;

import nextstep.subway.dto.LineChange;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public static Line of(LineRequest lineRequest) {
        Line line = new Line();
        line.name = lineRequest.getName();
        line.color = lineRequest.getColor();
        line.lineStations.init(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        return line;
    }

    public void update(LineChange lineChange) {
        if (StringUtils.hasText(lineChange.getName())) {
            this.name = lineChange.getName();
        }
        if (StringUtils.hasText(lineChange.getColor())) {
            this.color = lineChange.getColor();
        }
    }

    public void removeStations() {
        this.lineStations.remove();
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

    public LineStations getLineStations() {
        return lineStations;
    }

    public void addLineStation(LineStation lineStation) {
        lineStations.add(lineStation);
    }
}
