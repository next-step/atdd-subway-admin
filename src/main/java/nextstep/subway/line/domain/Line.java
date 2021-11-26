package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.station.domain.Station;

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

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param lineStation
     */
    public void addSection(LineStation lineStation) {
        this.lineStations.add(lineStation);
        if (!lineStation.equalsLine(this)) {
            lineStation.toLine(this);
        }
    }

    public void removeSection(LineStation lineStation) {
        this.lineStations.remove(lineStation);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<LineStation> getSortedSections() {
        return lineStations.getSortedList();
    }

    public boolean containsSection(LineStation lineStation) {
        return this.lineStations.contains(lineStation);
    }

    public Line addSection(Distance distance, Station upStation, Station downStation) {
        LineStation upLineStation = lineStations.findByStation(upStation);
        LineStation downLineStation = lineStations.findByStation(downStation);

        validate(upLineStation, downLineStation);

        if (upLineStation != null) {
            addDownSection(distance, upLineStation, downStation);
            return this;
        }

        if (downLineStation != null) {
            addUpSection(distance, downLineStation, upStation);
            return this;
        }
        return this;
    }

    private void addDownSection(Distance distance, LineStation upLineStation, Station downStation) {
        if (upLineStation.isDownStation()) {
            upLineStation.update(distance, LineStationType.MIDDLE, downStation);
            LineStation downLineStation = LineStation.fromDownSection(downStation, this);
            lineStations.add(downLineStation);
            return;
        }

        addMiddleSection(upLineStation, downStation, distance, upLineStation.getNextStation());
    }

    private void addUpSection(Distance distance, LineStation downLineStation, Station upStation) {
        if (downLineStation.isUpStation()) {
            downLineStation.update(LineStationType.MIDDLE);
            LineStation upLineStation =
                LineStation.ofUpSection(upStation, this, distance, downLineStation.getStation());
            lineStations.add(upLineStation);
            return;
        }

        LineStation upLineStation = lineStations.findByNextStation(downLineStation.getStation());
        addMiddleSection(upLineStation, upStation, distance, downLineStation.getStation());
    }

    private void addMiddleSection(LineStation updateLineStation, Station addStation, Distance distance,
        Station linkStation) {
        updateLineStation.update(updateLineStation.calculateDistance(distance), addStation);
        LineStation middleLineStation = LineStation
            .ofMiddleSection(addStation, this, distance, linkStation);
        lineStations.add(middleLineStation);
    }

    private void validate(LineStation upLineStation, LineStation downLineStation) {
        if (upLineStation == null && downLineStation == null) {
            throw new BusinessException(Messages.NOT_INCLUDE_SECTION.getValues());
        }

        if (upLineStation != null && downLineStation != null) {
            throw new BusinessException(Messages.ALREADY_EXISTS_SECTION.getValues());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
