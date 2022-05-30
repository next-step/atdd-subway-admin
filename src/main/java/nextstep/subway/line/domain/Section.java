package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
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

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {}

    public static Section of(Station upStation, Station downStation, int distance) {
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

    /*
    애오개----------마포 => 공덕-----마포(update)
    애오개-----공덕(new)
     */
    public void update(Section newSection) {
        if (this.upStation.equals(newSection.getUpStation())) {
            this.upStation = newSection.getDownStation();
            updateDistance(newSection);
        }
        if (this.downStation.equals(newSection.getDownStation())) {
            this.downStation = newSection.getUpStation();
            updateDistance(newSection);
        }
    }

    private void updateDistance(Section newSection) {
        if (this.distance <= newSection.distance) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
        this.distance -= newSection.distance;
    }

    public List<Station> findStations() {
        return Arrays.asList(upStation, downStation);
    }
}
