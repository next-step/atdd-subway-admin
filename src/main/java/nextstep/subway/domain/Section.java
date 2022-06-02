package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Line line;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;
    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean modifyBy(Section section) {
        if (upStation == section.upStation) {
            modifyUpStation(section);
            return true;
        }
        if (downStation == section.downStation) {
            modifyDownStation(section);
            return true;
        }
        return false;
    }

    private void modifyUpStation(Section section) {
        decreaseBy(section.distance);
        upStation = section.downStation;
    }

    private void modifyDownStation(Section section) {
        decreaseBy(section.distance);
        downStation = section.upStation;
    }

    private void decreaseBy(int length) {
        if (distance <= length) {
            throw new IllegalArgumentException("구간의 길이가 너무 깁니다.");
        }
        distance -= length;
    }

    public boolean matchOutside(Section section) {
        return upStation == section.downStation || downStation == section.upStation;
    }

    public boolean matchInside(Section section) {
        return upStation == section.upStation || downStation == section.downStation;
    }

    public boolean contains(Station station) {
        return upStation == station || downStation == station;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
