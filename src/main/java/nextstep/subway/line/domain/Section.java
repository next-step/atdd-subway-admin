package nextstep.subway.line.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//@Entity
public class Section {
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     */
   //@Id
    private Long upStation;
   //@Id
    private Long downStation;
    private int distance;

    public Section() {

    }

    public Section(Long upStation, Long downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long upStation, Long downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getUpStation() {
        return upStation;
    }

    public Long getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
