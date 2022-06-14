package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private long distance;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, long distance) {
        validateDistance(distance);
        validateNotSameStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public void mergeWith(Section nextSection) {
        validateCorrectNextSection(nextSection);
        distance += nextSection.getDistance();
        downStation = nextSection.getDownStation();
    }

    private void validateCorrectNextSection(Section nextSection) {
        if (!downStation.equals(nextSection.getUpStation())) {
            throw new IllegalArgumentException("구간을 합칠 수 없습니다.");
        }
    }

    private void validateDistance(long distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리는 1 이상의 숫자만 입력이 가능합니다.");
        }
    }

    private void validateNotSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 동일한 역으로 지정될 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
