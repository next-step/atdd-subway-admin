package nextstep.subway.line.domain;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long upStation;
    private Long downStation;
    private int distance;

    @ManyToOne
    private Line line;

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

    public void setLine(Line line) {
        this.line = line;
        line.getSections().add(this);
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
