package nextstep.subway.section;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.ValueOutOfBoundsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station_section.StationSection;
import nextstep.subway.station_section.StationSectionsBySection;
import nextstep.subway.station_section.StationType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @Embedded
    private StationSectionsBySection stationSectionsBySection = new StationSectionsBySection(new HashSet<>());

    protected Section() {}

    private Section(int distance) {
        this.distance = distance;
    }

    public static Section create(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation, distance);
        Section section = new Section(distance);
        section.setLine(line);
        StationSection.create(upStation, section, StationType.UP);
        StationSection.create(downStation, section, StationType.DOWN);
        return section;
    }

    private static void validate(Line line, Station upStation, Station downStation, int distance) {
        if (Objects.isNull(line)) {
            throw new NullPointerException("노선이 존재하지 않습니다.");
        }

        if (Objects.isNull(upStation)) {
            throw new NullPointerException("상행역이 존재하지 않습니다.");
        }

        if (Objects.isNull(downStation)) {
            throw new NullPointerException("하행역이 존재하지 않습니다.");
        }

        if (distance < 1) {
            throw new ValueOutOfBoundsException("구간의 길이는 0보다 커야합니다.", "distance", String.valueOf(distance), null);
        }
    }

    public void setLine(Line line) {
        this.line = line;
        line.addSections(this);
    }

    public void addStationSection(StationSection stationSection) {
        stationSectionsBySection.add(stationSection);
    }

    public String upStationName() {
        return stationSectionsBySection.upStationName();
    }

    public String downStationName() {
        return stationSectionsBySection.downStationName();
    }

    public int distance() {
        return distance;
    }

    public StationSectionsBySection stationSections() {
        return stationSectionsBySection;
    }

    public Long getId() {
        return id;
    }

    public int stationSectionsSize() {
        return stationSectionsBySection.size();
    }
}
