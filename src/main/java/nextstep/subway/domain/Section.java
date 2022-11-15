package nextstep.subway.domain;

import javax.persistence.*;

import static nextstep.subway.exception.ErrorMessage.DISTANCE_CANNOT_BE_ZERO;
import static nextstep.subway.exception.ErrorMessage.SAME_SUBWAY_SECTION_ERROR;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    protected Section() {

    }

    public Section(Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private static void validateSection(Station upStation, Station downStation, int distance) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_SUBWAY_SECTION_ERROR.getMessage());
        }
        if (distance < 0) {
            throw new IllegalArgumentException(DISTANCE_CANNOT_BE_ZERO.getMessage());
        }
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
