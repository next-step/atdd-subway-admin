package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Station prevStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Section nextStation;

    private int distance;

    protected Section() {
    }

    public Section(Station prevStation, Section nextStation, int distance) {
        validateSection(prevStation, nextStation, distance);
        this.prevStation = prevStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    private void validateSection(Station prevStation, Section nextStation, int distance) {
        if (Objects.isNull(prevStation) || Objects.isNull(nextStation)) {
            throw new IllegalArgumentException("양 끝 Station이 반드시 존재해야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("거리값은 반드시 0보다 커야 합니다.");
        }
    }
}
