package nextstep.subway.domain;

import java.util.Optional;
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

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void setFinalStations(final Station finalUpStation, final Station finalDownStation, final Long distance) {
        relateToStation(finalUpStation, null, 0L, finalDownStation, distance);
        relateToStation(finalDownStation, finalUpStation, distance, null, 0L);
        sections.add(new Section(this, finalUpStation, finalDownStation, distance));
    }

    public void update(final String newName, final String newColor) {
        name = newName;
        color = newColor;
    }

    public SectionResponse registerSection(final Station upStation, final Station downStation, final Long distance) {
        final Optional<LineStation> upRelation = lineStations.getByStation(upStation);
        final Optional<LineStation> downRelation = lineStations.getByStation(downStation);
        validateStations(upRelation, downRelation);
        if (upRelation.isPresent()) {
            relateToStationWithPrevious(upRelation.get(), downStation, distance);
        }
        if (downRelation.isPresent()) {
            relateToStationWithNext(downRelation.get(), upStation, distance);
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

    public Sections getSections() {
        return sections;
    }

    private LineStation relateToStation(final Station newStation,
                                        final Station previous,
                                        final Long distanceToPrevious,
                                        final Station next,
                                        final Long distanceToNext) {
        final LineStation newRelation = new LineStation(
                this,
                newStation,
                previous,
                distanceToPrevious,
                next,
                distanceToNext);
        lineStations.add(newRelation);
        return newRelation;
    }

    private void validateStations(final Optional<LineStation> upRelation, final Optional<LineStation> downRelation) {
        if (upRelation.isPresent() && downRelation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
        if (!upRelation.isPresent() && !downRelation.isPresent()) {
            throw new IllegalArgumentException("적어도 1개 지하철역은 기존에 등록된 역이어야 합니다");
        }
    }

    private void validateDistance(final Long existingDistance, final Long distance) {
        if (existingDistance <= distance) {
            throw new IllegalArgumentException("새로운 구간의 거리는 기존 구간의 거리보다 짧아야 합니다");
        }
    }

    private void relateToStationWithPrevious(final LineStation previousRelation,
                                             final Station newStation,
                                             final Long distance) {
        final LineStation newRelation = relateToStation(newStation, previousRelation.getStation(), distance, null, 0L);
        setOldNextRelation(lineStations.getByStation(previousRelation.getNext()), newRelation, distance);
        previousRelation.updateNext(newStation, distance);
    }

    private void setOldNextRelation(final Optional<LineStation> oldNextRelation,
                                    final LineStation newRelation,
                                    final Long distance) {
        if (!oldNextRelation.isPresent()) {
            return;
        }
        validateDistance(oldNextRelation.get().getDistanceToPrevious(), distance);
        final Station oldNext = oldNextRelation.get().getStation();
        final long distanceToOldNext = oldNextRelation.get().getDistanceToPrevious() - distance;
        oldNextRelation.get().updatePrevious(newRelation.getStation(), distanceToOldNext);
        newRelation.updateNext(oldNext, distanceToOldNext);
    }

    private void relateToStationWithNext(final LineStation nextRelation,
                                         final Station newStation,
                                         final Long distance) {
        final LineStation newRelation = relateToStation(newStation, null, 0L, nextRelation.getStation(), distance);
        setOldPreviousRelation(lineStations.getByStation(nextRelation.getPrevious()), newRelation, distance);
        nextRelation.updatePrevious(newStation, distance);
    }

    private void setOldPreviousRelation(final Optional<LineStation> oldPreviousRelation,
                                        final LineStation newRelation,
                                        final Long distance) {
        if (!oldPreviousRelation.isPresent()) {
            return;
        }
        validateDistance(oldPreviousRelation.get().getDistanceToNext(), distance);
        final Station oldPrevious = oldPreviousRelation.get().getStation();
        final long distanceToOldPrevious = oldPreviousRelation.get().getDistanceToNext() - distance;
        oldPreviousRelation.get().updateNext(newRelation.getStation(), distanceToOldPrevious);
        newRelation.updatePrevious(oldPrevious, distanceToOldPrevious);
    }
}
