package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINE_ID")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;

    @Column(name = "distance")
    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void validateCheck(Section section) {
        duplicateValidateCheck(section);
        mismatchValidateCheck(section);

        reregisterUpStation(section);
        reregisterDownStation(section);
    }

    private void mismatchValidateCheck(Section section) {
        if (!this.upStation.isSame(section) && !this.downStation.isSame(section)) {
            throw new IllegalArgumentException("상행역과 하행역 모두 현재 노선에 존재하지 않습니다.");
        }
    }

    private void duplicateValidateCheck(Section section) {
        if (this.upStation.equals(section.getUpStation()) && this.downStation.equals(section.getDownStation())) {
            throw new IllegalArgumentException("이미 등록된 정보 입니다.");
        }
    }

    private void reregisterDownStation(Section section) {
        if (this.downStation.equals(section.getDownStation())) {
            distanceValidateCheck(section);
            changeDistance(this.distance - section.getDistance());
            changeDownStation(section.getUpStation());
        }
    }

    private void reregisterUpStation(Section section) {
        if (this.upStation.equals(section.getUpStation())) {
            distanceValidateCheck(section);
            changeDistance(this.distance - section.getDistance());
            changeUpStation(section.getDownStation());
        }
    }

    private void distanceValidateCheck(Section section) {
        if (this.distance <= section.getDistance()) {
            throw new IllegalArgumentException("기존 역 사이 길이 보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    public void changeDistance(int distance) {
        this.distance = distance;
    }

    public void changeUpStation(Station downStation) {
        this.upStation = downStation;
    }

    private void changeDownStation(Station upStation) {
        this.downStation = upStation;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        if (getId() != null && Objects.equals(getId(), section.getId())) return true;
        return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId()) && Objects.equals(getLine(), section.getLine()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLine(), getUpStation(), getDownStation(), getDistance());
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", distance=" + distance +
                '}';
    }
}
