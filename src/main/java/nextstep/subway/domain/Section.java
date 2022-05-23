package nextstep.subway.domain;

import javax.persistence.*;

import static java.util.Objects.requireNonNull;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private Long distance;

    public Section(Station upStation, Station downStation, Long distance) {
        this.upStation = requireNonNull(upStation, "상행 지하철역이 비었습니다");
        this.downStation = requireNonNull(downStation, "하행 지하철역이 비었습니다");
        this.distance = requireNonNull(distance, "구간 길이가 비었습니다");
    }

    protected Section() {
    }

    public void bindLine(Line line) {
        requireNonNull(line, "지하철 노선이 비었습니다");
        this.lineId = line.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
