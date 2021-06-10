package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    private Station prevStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Section nextStation;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station prevStation, Section nextStation, int distance) {
        validateSection(line, prevStation, nextStation, distance);

        this.line = line;
        this.prevStation = prevStation;
        this.nextStation = nextStation;
        this.distance = distance;

        line.addSection(this);
    }

    private void validateSection(Line line, Station prevStation, Section nextStation, int distance) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("소속될 Line이 반드시 존재해야 합니다.");
        }

        if (Objects.isNull(prevStation) || Objects.isNull(nextStation)) {
            throw new IllegalArgumentException("양 끝 Station이 반드시 존재해야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("거리값은 반드시 0보다 커야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
