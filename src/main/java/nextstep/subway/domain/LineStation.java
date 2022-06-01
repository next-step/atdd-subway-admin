package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private boolean isStart;

    private boolean isLast;

    protected LineStation() {}

    public LineStation(Long distance, Station upStation, Station downStation, boolean isFirstAdd) {
        validation(distance, upStation, downStation);

        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;

        if (isFirstAdd) {
            this.isStart = true;
            this.isLast = true;
        }
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation.isSameId(upStation.getId());
    }

    public boolean isAddNewFirst(Station downStation) {
        return this.isStart && this.upStation.isSameId(downStation.getId());
    }

    public boolean isSameDownStation(Station downStation) {
        return this.downStation.isSameId(downStation.getId());
    }

    public boolean isAddNewLast(Station upStation) {
        return this.isLast && this.downStation.isSameId(upStation.getId());
    }

    public LineStation addStation(Station upStation, Station downStation, Long distance) {
        LineStation addResult = new LineStation(distance, upStation, downStation, false);

        if (isAddNewFirst(downStation)) {
            this.isStart = false;
            addResult.isStart = true;

            return addResult;
        }
        if (isAddNewLast(upStation)) {
            this.isLast = false;
            addResult.isLast = true;

            return addResult;
        }
        if (this.upStation.isSameId(upStation.getId())) {
            addResult.isStart = this.isStart;
            this.isStart = false;
            this.distance -= distance;
            this.upStation = downStation;
        }
        if (this.downStation.isSameId(downStation.getId())) {
            addResult.isLast = this.isLast;
            this.isLast = false;
            this.distance -= distance;
            this.downStation = upStation;
        }

        return addResult;
    }

    private void validation(Long distance, Station upStation, Station downStation) {
        // spring validator 로 대체 가능
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0 보다 커야 합니다.");
        }
        if (upStation == null) {
            throw new IllegalArgumentException("상행역 정보는 필수입니다.");
        }
        if (downStation == null) {
            throw new IllegalArgumentException("하행역 정보는 필수입니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isLast() {
        return isLast;
    }
}
