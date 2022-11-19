package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id")
    private Station preStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    private Integer distance;

    protected Section(){

    }

    public Section(Station preStation, Station station, Integer distance) {
        validateSameStation(preStation, station);
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
    }

    private void validateSameStation(Station preStation, Station station) {
        if(station.equals(preStation)){
            throw new IllegalArgumentException("역 구간 생성 시, 상행역과 하행역이 같은 수 없습니다.");
        }
    }

    public void updateSection(Station preStation, Station station, Integer distance){
        validateDistance(preStation, distance);
        this.preStation = preStation;
        this.station = station;
        this.distance -= distance;
    }

    public void linkPreSectionByDelete(Section targetSection){
        this.preStation = targetSection.preStation;
        this.distance += targetSection.distance;
    }

    private void validateDistance(Station preStation, Integer distance) {
        if(preStation !=null && this.distance <= distance){
            throw new IllegalArgumentException("신규 등록 구간 거리는 기존 거리보다 크면 안됩니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Station getPreStation() {
        return preStation;
    }

    public Station getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }
}
