package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.exception.IllegalStationException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_upstation"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_downstation"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {
    }

    private Section(final Station upStation, final Station downStation, final int distance) {
        validateStationNotNull(upStation);
        validateStationNotNull(downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    private void validateStationNotNull(Station station){
        if (station == null) {
            throw new IllegalStationException();
        }
    }

    public static Section of(final Station upStation, final Station downStation, final int distance) {
        return new Section(upStation, downStation, distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void addLine(final Line line) {
        this.line = line;
    }
}
