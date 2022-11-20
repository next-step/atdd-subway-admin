package nextstep.subway.domain;

import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    private int distance;

    protected Section() {}

    public Section(Station station) {
        this.downStation = station;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void changed(Section infixSection) {
        changedStation(infixSection);
    }

    private void changedStation(Section infixSection) {
        if (upStation.equalsById(infixSection.upStation)) {
            if (isLessThanDistance(infixSection)) {
                throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
            }
            checkSameOtherStation(downStation, infixSection.downStation);
            this.upStation = infixSection.downStation;
            changedDistance(infixSection);
        }
        if (downStation.equalsById(infixSection.downStation)) {
            if (isLessThanDistance(infixSection)) {
                throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
            }
            checkSameOtherStation(upStation, infixSection.upStation);
            this.downStation = infixSection.upStation;
            changedDistance(infixSection);
        }
    }

    private void checkSameOtherStation(Station station, Station targetStation) {
        if (station.equalsById(targetStation)) {
            throw new InvalidParameterException("상행역과 하행역이 모두 노선에 등록되어 있는 경우 새롭게 등록할 수 없습니다.");
        }
    }

    private void changedDistance(Section infixSection) {
        this.distance = distance - infixSection.distance;
    }

    public boolean isLessThanDistance(Section infixSection) {
        return infixSection.distance > this.distance;
    }

    public boolean isUpStationEqualsId(Long id) {
        return upStation.getId().equals(id);
    }

    public boolean isDownStationEqualsId(Long id) {
        return downStation.getId().equals(id);
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }

}
