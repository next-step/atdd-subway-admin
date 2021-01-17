package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private int distance;

    public Section() {

    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void update(Section section) {
        this.upStation = section.upStation;
        this.downStation = section.downStation;
        this.distance = section.distance;
    }

    public void setLine(Line line) {
        // 기존 지하철 노선 관계를 제거
        if (this.line != null) {
            this.line.getSections().removeSection(this);
        }
        this.line = line;
        line.getSections().addSection(this);
    }

}
