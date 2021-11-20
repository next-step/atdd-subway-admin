package nextstep.subway.section.domain;

import com.sun.tools.internal.xjc.reader.relaxng.RELAXNGCompiler;
import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

import java.util.Objects;
import java.util.Optional;

import static javax.persistence.FetchType.*;

/**
 * packageName : nextstep.subway.section
 * fileName : Section
 * author : haedoang
 * date : 2021/11/20
 * description : 구간 엔티티
 */
@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LINE_ID")
    private Line line;


    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        if(Objects.isNull(line)) {
            throw new NotFoundException("노선이 존재하지 않습니다.");
        }

        if(Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new NotFoundException("역이 존재하지 않습니다.");
        }
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
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
}
