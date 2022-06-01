package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Column(nullable = false)
    private Integer distance;

    @ManyToOne()
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Station> allStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void updateUpStation(Section section) {
        subtractDistance(section.getDistance());
        this.upStation = section.getDownStation();
    }

    public void updateDownStation(Section section) {
        subtractDistance(section.getDistance());
        this.downStation = section.getUpStation();
    }

    public void subtractDistance(Integer distance) {
        validateSubtractDistance(distance);
        this.distance = Math.subtractExact(this.distance, distance);

    }

    private void validateSubtractDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
    }

    public boolean duplicateUpDownStations(Section section) {
        List<Station> stations = allStations();
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    public boolean containsUpDownStations(Section section) {
        List<Station> stations = allStations();
        return stations.contains(section.getUpStation()) || stations.contains(section.getDownStation());
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

    public Integer getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line=" + line +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Section section = (Section) o;

        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
