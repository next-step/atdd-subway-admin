package nextstep.subway.domain;

import javax.persistence.*;

@Entity
@Table(name = "line_station")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;
    private Integer distance;

    public Section(Line line, Integer distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    protected Section() {
    }

    public Integer getDistance() {
        return this.distance;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public void updateDistance(int distance) {
        this.distance = distance;
    }

    public void connect(Section section) {
        if (this.downStation == section.getDownStation()) {
            updateDownStation(section.getUpStation());
        }

        if (this.upStation == section.getUpStation()) {
            updateUpStation(section.getDownStation());
        }

        checkDistance(section);
        updateDistance(getDistance() - section.getDistance());
    }

    private void checkDistance(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("distance 는 구간 내에 속할 수 있는 값 이어야 합니다.");
        }
    }
}
