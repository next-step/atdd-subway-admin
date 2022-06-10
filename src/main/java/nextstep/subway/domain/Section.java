package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

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
    private int distance;

    public Section(Line line, int distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    protected Section() {
    }

    public int getDistance() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(getUpStation(), section.getUpStation())
                && Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, getUpStation(), getDownStation());
    }
}
