package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void changeUpStation(final Station station) {
        this.upStation = station;
    }

    public void changeDownStation(final Station station) {
        this.downStation = station;
    }

    public void changeDistance(final int distance) {
        this.distance.change(distance);
    }

    public void validateDistance(final Section newSection) {
        distance.equalToOrLessThan(newSection.getDistance());
    }

    public void registerEndPoint() {
        registerAscentEndPoint();
        registerDeAscentEndPoint();
    }

    public void registerAscentEndPoint() {
        upStation.changeAscentEndPoint(true);

    }

    public void registerDeAscentEndPoint() {
        downStation.changeDeAscentEndPoint(false);
    }


    public boolean isAscentEndpoint(final Section originalSection) {
        return originalSection.getUpStation().equals(downStation);
    }

    public boolean isDeAscentEndpoint(final Section originalSection) {
        return originalSection.getDownStation().equals(upStation);
    }

    public boolean isAscentEndpoint() {
        return upStation.isAscentEndPoint();
    }

    public boolean isDeAscentEndpoint() {
        return upStation.isDeAscentEndPoint();
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
}
