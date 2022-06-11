package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    @Embedded
    private Distance distance;
    @Column(name = "final_up_station")
    private boolean finalUpStation;
    @Column(name = "final_down_station")
    private boolean finalDownStation;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(final Station upStation, final Station downStation, final long distance) {
        return new Section(upStation, downStation, distance);
    }

    public void updateLine(final Line line) {
        this.line = line;
    }

    public void updateFinalUpStation(final boolean isFinalUpStation) {
        this.finalUpStation = isFinalUpStation;
    }

    public void updateFinalDownStation(final boolean isFinalDownStation) {
        this.finalDownStation = isFinalDownStation;
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

    public long getDistance() {
        return distance.getValue();
    }

    public boolean isFinalUpStation() {
        return finalUpStation;
    }

    public boolean isFinalDownStation() {
        return finalDownStation;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line.getId() +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", finalUpStation=" + finalUpStation +
                ", finalDownStation=" + finalDownStation +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;
        return finalUpStation == section.finalUpStation && finalDownStation == section.finalDownStation && Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance, finalUpStation, finalDownStation);
    }
}


