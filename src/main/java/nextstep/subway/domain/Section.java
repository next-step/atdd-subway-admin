package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.InvalidLineException;
import nextstep.subway.exception.InvalidStringException;

@Entity
public class Section extends BaseEntity {

    private static final String INVALID_LINE_MESSAGE = "노선 정보가 없습니다.";
    private static final String INVALID_LINE_UP_STATION = "노선 상행역 정보가 존재하지 않습니다.";
    private static final String INVALID_LINE_DOWN_STATION = "노선 하행역 정보가 존재하지 않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        validate(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Distance newDistance(Section upSection, Section downSection) {
        return upSection.distance
            .add(downSection.distance);
    }

    private void validate(Station upStation, Station downStation) {
        validateUpStation(upStation);
        validateDownStation(downStation);
    }

    private void validateUpStation(Station upStation) {
        if (Objects.isNull(upStation)) {
            throw new InvalidStringException(INVALID_LINE_UP_STATION);
        }
    }

    private void validateDownStation(Station downStation) {
        if (Objects.isNull(downStation)) {
            throw new InvalidStringException(INVALID_LINE_DOWN_STATION);
        }
    }

    public boolean isSameStationPair(Section target) {
        return this.upStation.equals(target.upStation)
            && this.downStation.equals(target.downStation);
    }

    public List<Station> getStationPair() {
        return Arrays.asList(this.upStation, this.downStation);
    }

    public void update(Section target) {
        if (this.upStation.equals(target.upStation)) {
            this.upStation = target.downStation;
            this.distance = subtractDistance(target);
        }
        if (this.downStation.equals(target.downStation)) {
            this.downStation = target.upStation;
            this.distance = subtractDistance(target);
        }
    }

    private Distance subtractDistance(Section target) {
        return this.distance.subtract(target.distance);
    }

    private void validateLineNotNull(Line line) {
        if (Objects.isNull(line)) {
            throw new InvalidLineException(INVALID_LINE_MESSAGE);
        }
    }

    public boolean equalsUpStation(Station target) {
        return this.upStation.equals(target);
    }

    public boolean equalsDownStation(Station target) {
        return this.downStation.equals(target);
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        validateLineNotNull(line);
        this.line = line;
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

}
