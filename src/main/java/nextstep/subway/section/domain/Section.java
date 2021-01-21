package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

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

    public int getDistance() {
        return distance;
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

    public void validDuplicate(Line line) {
        List<Station> stations = line.getStations();
        boolean isUpStationExisted = stations.stream().anyMatch(it -> it == upStation);
        boolean isDownStationExisted = stations.stream().anyMatch(it -> it == downStation);

        if (isUpStationExisted && isDownStationExisted) {
            throw new RuntimeException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
    }

    public void validNotFound(Line line) {
        List<Station> stations = line.getStations();
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

}
