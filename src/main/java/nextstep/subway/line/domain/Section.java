package nextstep.subway.line.domain;

import lombok.Builder;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;


@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation) {
        validate(line, upStation, downStation);
        this.line = line;
        this.upStation = upStation;
    }

    @Builder
    private Section(Line line, Station upStation, Station downStation, int distance) {
        this(line, upStation, downStation);
        validate(distance);
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(Station downStation) {
        this.downStation = downStation;
    }

    public void update(int distance) {
        validate(distance);
        this.distance = distance;
    }

    private void validate(Line line, Station upStation, Station downStation) {
        if (line == null || upStation == null || downStation == null) {
            throw new IllegalArgumentException("필수값 누락입니다.");
        }
    }

    private void validate(int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }
}
