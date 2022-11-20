package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, Long distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = new Distance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public void rebase(Section section) {
        rebaseIfUpStationEquals(section);
        rebaseIfDownStationEquals(section);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    private void rebaseIfUpStationEquals(Section section) {
        if(this.upStation.equals(section.upStation)) {
            this.distance.sub(section.distance);
            upStation = section.downStation;
        }
    }

    private void rebaseIfDownStationEquals(Section section) {
        if(this.downStation.equals(section.downStation)) {
            this.distance.sub(section.distance);
            downStation = section.upStation;
        }
    }
}
