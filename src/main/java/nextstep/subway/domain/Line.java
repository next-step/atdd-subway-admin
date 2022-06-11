package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import nextstep.subway.dto.SectionResponse;

@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Embedded
    private LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void setFinalStations(final Station finalUpStation, final Station finalDownStation, final Long distance) {
        lineStations.addFinalStations(this, finalUpStation, finalDownStation, distance);
    }

    public void update(final String newName, final String newColor) {
        name = newName;
        color = newColor;
    }

    public SectionResponse registerSection(final Station upStation, final Station downStation, final Long distance) {
        validateDistance(distance);
        final boolean upStationExists = lineStations.hasRelationTo(upStation);
        final boolean downStationExists = lineStations.hasRelationTo(downStation);
        validateStations(upStationExists, downStationExists);
        if (upStationExists) {
            lineStations.addDownStation(this, upStation, downStation, distance);
        }
        if (downStationExists) {
            lineStations.addUpStation(this, upStation, downStation, distance);
        }
        return new SectionResponse(name, upStation.getName(), downStation.getName(), distance);
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

    public List<SectionResponse> getSections() {
        return lineStations.sections();
    }

    private void validateDistance(final Long distance) {
        if (distance <= 0L) {
            throw new IllegalArgumentException("등록할 구간의 거리는 0 이하일 수 없습니다");
        }
    }

    private void validateStations(final boolean upStationExists, final boolean downStationExists) {
        if (upStationExists && downStationExists) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
        if (!upStationExists && !downStationExists) {
            throw new IllegalArgumentException("적어도 1개 지하철역은 기존에 등록된 역이어야 합니다");
        }
    }
}
