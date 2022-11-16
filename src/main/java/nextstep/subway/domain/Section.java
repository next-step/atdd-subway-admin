package nextstep.subway.domain;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DynamicInsert
@Entity
public class Section extends BaseEntity {
    @Id
    @Column(name = "section_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upstation_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downstation_id")
    private Station downStation;
    @Embedded
    private Distance distance;
    @ColumnDefault("false")
    private boolean isAscentEndPoint;
    @ColumnDefault("false")
    private boolean isDeAscentEndPoint;

    protected Section() {

    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance.getValue();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public void setLine(final Line line) {
        this.line = line;
    }

    public void setUpStation(final Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(final Station downStation) {
        this.downStation = downStation;
    }

    private void changeUpStation(final Station station) {
        this.upStation = station;
    }

    private void changeDownStation(final Station station) {
        this.downStation = station;
    }

    public void changeDistance(final int distance) {
        this.distance.change(distance);
    }

    public void validateDistance(final Section newSection) {
        if (distance.isEqualToOrLessThan(newSection.getDistance())) {
            throw new IllegalArgumentException(
                    "새로 생성할 구간의 간격" + newSection.getDistance() + "는 기존 구간 간격 " + distance + "보다 짧아야 합니다.");
        }
    }

    public void registerEndPoint() {
        changeAscentEndPoint(true);
        changeDeAscentEndPoint(true);
    }

    public boolean isAscentEndpoint(final Section newSection) {
        return upStation.equals(newSection.getDownStation());
    }

    public boolean isDeAscentEndpoint(final Section newSection) {
        return downStation.equals(newSection.getUpStation());
    }


    public void changeAscentEndPoint(final Boolean ascentEndPoint) {
        isAscentEndPoint = ascentEndPoint;
    }

    public void changeDeAscentEndPoint(final Boolean deAscentEndPoint) {
        isDeAscentEndPoint = deAscentEndPoint;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isInclude(final Long stationId) {
        return upStation.getId().equals(stationId) || downStation.getId().equals(stationId);
    }

    public void reArrangeWith(final Section newSection) {
        if (changeEndPoint(newSection)) return;
        changeStation(newSection);
    }

    private void changeStation(final Section newSection) {
        if (isSameUpStation(newSection)) {
            changeUpStationToDownStationOf(newSection);
            return;
        }
        if (isSameDownStation(newSection)) {
            changeDownStationToUpStationOf(newSection);
        }
    }

    private boolean changeEndPoint(final Section newSection) {
        if (isAscentEndpoint(newSection)) {
            changeAscentEndPointTo(newSection);
            return true;
        }
        if (isDeAscentEndpoint(newSection)) {
            changeDeAscentEndPointTo(newSection);
            return true;
        }
        return false;
    }

    private void changeDeAscentEndPointTo(final Section newSection) {
        changeDeAscentEndPoint(false);
        newSection.changeDeAscentEndPoint(true);
    }

    private void changeAscentEndPointTo(final Section newSection) {
        changeAscentEndPoint(false);
        newSection.changeAscentEndPoint(true);
    }

    private void changeDownStationToUpStationOf(final Section newSection) {
        changeDownStation(newSection.getUpStation());
        minusDistanceOf(newSection);
    }

    private void changeUpStationToDownStationOf(final Section newSection) {
        changeUpStation(newSection.getDownStation());
        minusDistanceOf(newSection);
    }

    private void minusDistanceOf(final Section newSection) {
        distance.minus(newSection.getDistance());
    }

    public boolean isSameDownStation(final Section newSection) {
        return downStation.equals(newSection.getDownStation());
    }

    public boolean isSameUpStation(final Section newSection) {
        return upStation.equals(newSection.getUpStation());
    }

    public boolean isConnectableWith(final Section other) {
        return upStation.equals(other.getDownStation());
    }

    public void setDownStation(final Section section) {
        setDownStation(section.getDownStation());
    }

    public void setUpStation(final Section section) {
        setUpStation(section.getUpStation());
    }

    public boolean isAscentEndpoint() {
        return isAscentEndPoint;
    }

    public boolean isDeAscentEndpoint() {
        return isDeAscentEndPoint;
    }
}
