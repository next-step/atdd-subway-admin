package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Section extends BaseEntity {

    @Id
    @Column(name = "section_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    private long distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public List<Station> getStations() {
        ArrayList<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public long getDistance() {
        return distance;
    }

    public boolean isShortDistance(Section section) {
        if (distance <= section.getDistance()) {
            return true;
        }
        return false;
    }

    public boolean isSameSection(Section section) {
        return isSameUpStation(section) && isSameDownStation(section);
    }

    public boolean isSameUpStation(Section newSection) {
        return upStation.equals(newSection.getUpStation());
    }

    public boolean isSameDownStation(Section newSection) {
        return downStation.equals(newSection);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isComponentAllOfStations(List<Station> stations) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public boolean isComponentAnyOfStations(List<Station> stations) {
        return stations.contains(upStation) || stations.contains(downStation);
    }

    public void modifyUpStation(Section newSection) {
        this.upStation = newSection.getDownStation();
        this.distance -= newSection.getDistance();
    }

    public void modifyDownStation(Section newSection) {
        this.downStation = newSection.getUpStation();
        this.distance -= newSection.getDistance();
    }
}
