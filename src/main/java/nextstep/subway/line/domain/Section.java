package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {}

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void update(Section newSection) {
        if (isEqualUpStation(newSection.getUpStation())) {
            updateUpStation(newSection);
        }

        if (isEqualDownStation(newSection.getDownStation())) {
            updateDownStation(newSection);
        }
    }

    private void updateUpStation(Section newSection) {
        this.upStation = newSection.getDownStation();
        updateDistance(newSection);
    }

    private void updateDownStation(Section newSection) {
        this.downStation = newSection.getUpStation();
        updateDistance(newSection);
    }

    private void updateDistance(Section newSection) {
        this.distance.subtract(newSection.distance);
    }

    public void addDistance(Section downSection) {
        this.distance.add(downSection.distance);
    }

    public List<Station> findStations() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean isEqualUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isEqualDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public Section rearrange(Section downSection) {
        this.distance.add(downSection.distance);
        return Section.of(this.upStation, downSection.downStation, this.distance);
    }
}
