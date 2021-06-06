package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section implements Comparable<Section> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private Integer distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isEqualToUpStation(Station station) {
        return upStation.equals(station);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    @Override
    public int compareTo(Section section) {
        if (this.upStation.equals(section.getUpStation()) || this.downStation.equals(section.getUpStation())) {
            return -1; //앞으로 간다.
        }

        if (this.downStation.equals(section.getDownStation()) || this.upStation.equals(section.getDownStation())) {
            return 1; //뒤로 간다.
        }

        return 0;
    }
}
