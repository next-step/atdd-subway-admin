package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;

    protected Section() {
    }

    public Section(Station upStation, Station downStation) {
        validateStation(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Section(Line line, Station upStation, Station downStation) {
        validateLine(line);
        validateStation(upStation, downStation);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateLine(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalStateException("Line은 필수입니다.");
        }
    }

    private void validateStation(Station upStation, Station downStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalStateException("upStation은 필수입니다.");
        }
        if (Objects.isNull(downStation)) {
            throw new IllegalStateException("downStation은 필수입니다.");
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void changeLine(Line line) {
        if (this.line != null) return;
        this.line = line;
        line.addSection(this);
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
    }
}
