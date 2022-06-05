package nextstep.subway.domain;

import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
        relateToStation(new LineStation(this, finalUpStation, null, finalDownStation));
        relateToStation(new LineStation(this, finalDownStation, finalUpStation, null));
        sections.add(new Section(this, finalUpStation, finalDownStation, distance));
    }

    public void update(final String newName, final String newColor) {
        name = newName;
        color = newColor;
    }

    public void relateToSection(final Station upStation, final Station downStation, final Long distance) {
        final Optional<LineStation> upRelation = lineStations.getByStation(upStation);
        final Optional<LineStation> downRelation = lineStations.getByStation(downStation);
        validateStations(upRelation, downRelation);
        if (!isFinalDownStation(upRelation) && !isFinalUpStation(downRelation)) {
            inCaseOfUpStationExisting(upRelation, downStation, distance);
            inCaseOfDownStationExisting(downRelation, upStation, distance);
        }
        if (isFinalDownStation(upRelation)) {
            upRelation.get().update(upRelation.get().getPrevious(), downStation);
            relateToStation(new LineStation(this, downStation, upStation, null));
        }
        if (isFinalUpStation(downRelation)) {
            downRelation.get().update(upStation, downRelation.get().getNext());
            relateToStation(new LineStation(this, upStation, null, downStation));
        }
        final Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
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

    private void relateToStation(final LineStation lineStation) {
        lineStations.add(lineStation);
    }

    private void validateStations(final Optional<LineStation> upRelation, final Optional<LineStation> downRelation) {
        if (upRelation.isPresent() && downRelation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 구간입니다");
        }
        if (!upRelation.isPresent() && !downRelation.isPresent()) {
            throw new IllegalArgumentException("적어도 1개 지하철역은 기존에 등록된 역이어야 합니다");
        }
    }

    private boolean isFinalUpStation(final Optional<LineStation> lineStation) {
        return lineStation.isPresent() && null == lineStation.get().getPrevious();
    }

    private boolean isFinalDownStation(final Optional<LineStation> lineStation) {
        return lineStation.isPresent() && null == lineStation.get().getNext();
    }

    private void inCaseOfUpStationExisting(final Optional<LineStation> upRelation,
                                           final Station newStation,
                                           final Long distance) {
        if (upRelation.isPresent()) {
            final Section existingSection = sections.getByUpStation(upRelation.get().getStation());
            validateDistance(existingSection, distance);
            existingSection.updateUpStation(newStation, existingSection.getDistance() - distance);

            upRelation.get().update(upRelation.get().getPrevious(), newStation);
            final LineStation downRelation = lineStations.getByStation(existingSection.getDownStation()).get();
            downRelation.update(newStation, downRelation.getNext());
            relateToStation(
                    new LineStation(this, newStation, upRelation.get().getStation(), downRelation.getStation()));
        }
    }

    private void inCaseOfDownStationExisting(final Optional<LineStation> downRelation,
                                             final Station newStation,
                                             final Long distance) {
        if (downRelation.isPresent()) {
            final Section existingSection = sections.getByDownStation(downRelation.get().getStation());
            validateDistance(existingSection, distance);
            existingSection.updateDownStation(newStation, existingSection.getDistance() - distance);

            downRelation.get().update(newStation, downRelation.get().getNext());
            final LineStation upRelation = lineStations.getByStation(existingSection.getUpStation()).get();
            upRelation.update(upRelation.getPrevious(), newStation);
            relateToStation(
                    new LineStation(this, newStation, upRelation.getStation(), downRelation.get().getStation()));
        }
    }

    private void validateDistance(final Section existingSection, final Long distance) {
        if (existingSection.getDistance() <= distance) {
            throw new IllegalArgumentException("새로운 구간의 거리는 기존 구간의 거리보다 짧아야 합니다");
        }
    }
}
