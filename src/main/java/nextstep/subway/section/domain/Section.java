package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.*;

import static nextstep.subway.common.utils.ValidationUtils.isNull;

@Entity
public class Section extends BaseEntity implements Comparable<Section>{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_upStation"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_downStation"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id" , nullable = false, foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @Embedded
    private Distance distance;

    protected Section () {}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        sectionValidation(line, upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    private void sectionValidation(Line line, Station upStation, Station downStation, int distance) {
        if (isNull(line)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "구간 정보가 없습니다.");
        }
        if (isNull(upStation)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "상행 정보가 없습니다.");
        }
        if (isNull(downStation)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "하행 정보가 없습니다.");
        }
    }

    @Override
    public int compareTo(Section o) {
        if (this.upStation.getId() > o.downStation.getId()) {
            return 1;
        }
        if (this.upStation.getId() < o.downStation.getId()) {
            return -1;
        }
        return 0;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

}
