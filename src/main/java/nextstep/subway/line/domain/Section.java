package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@EqualsAndHashCode(of = {"line", "upStation", "downStation"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line_station")
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_station_id")
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

    private Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(final Line line, final Station upStation, final Station downStation, final int distance) {
        return new Section(line, upStation, downStation, distance);
    }
}
