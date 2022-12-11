package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @Embedded
    private Distance distance;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        stations.add(this.upStation);
        stations.add(this.downStation);
        return stations;
    }

    public void reorganize(Section section) {
        reorganizeUpStation(section);
        reorganizeDownStation(section);
    }

    private void reorganizeDownStation(Section section) {
        if (isSameDownStation(section.downStation)) {
            this.downStation = section.upStation;
            this.distance = this.distance.subtract(section.distance);
        }
    }

    public boolean isSameDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    private void reorganizeUpStation(Section section) {
        if (isSameUpStation(section.upStation)) {
            this.upStation = section.downStation;
            this.distance = this.distance.subtract(section.distance);
        }
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(line, section.line) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    public boolean isConnectable(Section newSection) {
        return isSameUpStation(newSection.upStation) || isSameDownStation(newSection.downStation);
    }

}
