package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "upStation_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "downStation_id")
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Column
    private Long distance;


    public Section(Station upStation, Station downStation, Long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean existUpStation(Section section) {
        return (upStation.getId() == section.getUpStation().getId()
            || downStation.getId() == section.getUpStation().getId());
    }

    public boolean existDownStation(Section section) {
        return (upStation.getId() == section.getDownStation().getId()
            || downStation.getId() == section.getDownStation().getId());
    }


    protected Section() {

    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void updateUpStation(Section section) {
        if (upStation.equals(section.getUpStation())) {
            upStation = section.downStation;
            updateDistance(section);
        }
    }

    public void updateDownStation(Section section) {
        if (downStation.equals(section.getDownStation())) {
            updateDistance(section);
            downStation = section.upStation;
        }
    }

    public void updateDownStation(Station station) {
        downStation = station;
    }

    public void addDistance(Section section) {
        distance = distance + section.distance;
    }


    private void updateDistance(Section section) {
        if (distance <= section.distance) {
            throw new IllegalArgumentException("기존 구간 보다 거리가 멀 수 없습니다.");
        }
        distance = distance - section.distance;
    }

    public boolean hasUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean hasDownStation(Station station) {
        return downStation.equals(station);
    }
}
