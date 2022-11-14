package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.constant.ErrorCode;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_upstation"), nullable = false)
    private Station upStation;
    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_downstation"), nullable = false)
    private Station downStation;
    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;
    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Line line, Long distance) {
        validateLine(line);
        validateStations(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = Distance.from(distance);
    }

    public static Section of(Station upStation, Station downStation, Line line, Long distance) {
        return new Section(upStation, downStation, line, distance);
    }

    private void validateLine(Line line) {
        if(line == null) {
            throw new IllegalArgumentException(ErrorCode.노선_정보가_없음.getErrorMessage());
        }
    }

    private void validateStations(Station upStation, Station downStation) {
        validateUpStation(upStation);
        validateDownStation(downStation);
        if(upStation.equals(downStation)) {
            throw new IllegalArgumentException(ErrorCode.구간의_상행역과_하행역이_동일할_수_없음.getErrorMessage());
        }
    }

    private void validateUpStation(Station upstation) {
        if(upstation == null) {
            throw new IllegalArgumentException(ErrorCode.상행종착역은_비어있을_수_없음.getErrorMessage());
        }
    }

    private void validateDownStation(Station downStation) {
        if(downStation == null) {
            throw new IllegalArgumentException(ErrorCode.하행종착역은_비어있을_수_없음.getErrorMessage());
        }
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public boolean isSameUpStation(Station station) {
        return upStation.isSameStation(station);
    }

    public boolean isSameDownStation(Station station) {
        return downStation.isSameStation(station);
    }

    public void updateUpStation(Section section) {
        this.distance = this.distance.subtract(section.distance);
        this.upStation = section.downStation;
    }

    public void updateDownStation(Section section) {
        this.distance = this.distance.subtract(section.distance);
        this.downStation = section.upStation;
    }

    public Distance addDistance(Section section) {
        return this.distance.add(section.distance);
    }

    public Distance getDistance() {
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

    public boolean isSameSection(Section section) {
        return Objects.equals(this.id, section.id);
    }
}
