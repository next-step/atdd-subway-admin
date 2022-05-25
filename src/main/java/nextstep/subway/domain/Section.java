package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distance")
    private int distance;

    @OneToOne
    @JoinColumn(name = "STATION_ID")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "STATION_ID")
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "LINE_ID")
    private Line line;

    protected Section() {

    }

    public Section(int distance, Station upStation, Station downStation, Line line) {
        this.id = null;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        if (getId() != null && Objects.equals(getId(), section.getId())) return true;
        return getDistance() == section.getDistance() && Objects.equals(getId(), section.getId()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(getLine(), section.getLine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDistance(), getUpStation(), getDownStation(), getLine());
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", distance=" + distance +
                '}';
    }
}
