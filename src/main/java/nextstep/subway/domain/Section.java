package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    private int sortNo = 1000;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    protected Section() {
    }

    public Section(Line line, int distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = new Distance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Line line, int distance, int sortNo, Station upStation, Station downStation) {
        this.line = line;
        this.distance = new Distance(distance);
        this.sortNo = sortNo;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getSortNo() {
        return this.sortNo;
    }

    public boolean isEqualsUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isEqualsDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section that = (Section) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Section createNewSection(int distance, int sortNo, Station upStation, Station downStation) {
        return new Section(line, distance, sortNo, upStation, downStation);
    }

    public Section createNewAppendDownStation(int distance, int sortNo, Station downStation) {
        return new Section(line, distance, sortNo, this.downStation, downStation);
    }

    public Section createNewPrependUpStation(int distance, int sortNo, Station upStation) {
        return new Section(line, distance, sortNo, upStation, this.upStation);
    }

    public void validateLength(int distance) {
        if (this.distance.isGreaterEqual(distance)) {
            throw new IllegalArgumentException("기존역 사이에 크거나 같은 길이의 역을 등록할 수 없습니다.");
        }
    }

    public void validateAlreadyExistsStation(Station upStation, Station downStation) {
        if (this.upStation.equals(upStation) && this.downStation.equals(downStation)) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
    }

    public void validateNotExistsStation(Station upStation, Station downStation) {
        if (!this.upStation.equals(upStation) && !this.downStation.equals(downStation) && !this.upStation
                .equals(downStation) && !this.downStation.equals(upStation)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    public void ifGreaterThanThenPlusSortNo(int sortNo) {
        if (this.sortNo > sortNo) {
            this.sortNo++;
        }
    }

    public void ifGreaterThanThenMinusSortNo(int sortNo) {
        if (this.sortNo > sortNo) {
            this.sortNo--;
        }
    }

    public void ifLessThanThenPlusSortNo(int sortNo) {
        if (this.sortNo < sortNo) {
            this.sortNo--;
        }
    }

    public int getMinusDistance(int distance) {
        return this.distance.getMinusDistance(distance);
    }
}
