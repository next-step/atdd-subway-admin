package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = {"line", "upStation", "downStation"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line_station")
@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(name = "distance")
    private int distance;

    private LineStation(final Line line, final Station upStation, final Station downStation, final int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static LineStation of(final Line line, final Station upStation, final Station downStation, final int distance) {
        return new LineStation(line, upStation, downStation, distance);
    }

    public boolean isUpStation(final Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isDownStation(final Station downStation) {
        return this.downStation.equals(downStation);
    }
}
