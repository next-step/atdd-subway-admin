package nextstep.subway.section.domain;

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

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    private static final String CREATION_FAIL_MESSAGE = "Section 생성에 필요한 필수 정보를 확인해주세요. upStation=%s, downStation=%s, distance=%s";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {}

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        validateCreateSection(upStation, downStation, distance);
        return new Section(upStation, downStation, distance);
    }

    public void registerLine(Line line) {
        this.line = line;
    }

    public boolean isUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isSameDistance(Distance distance) {
        return this.distance.equals(distance);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    private static void validateCreateSection(Station upStation, Station downStation, Distance distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation) || Objects.isNull(distance)) {
            throw new IllegalArgumentException(String.format(CREATION_FAIL_MESSAGE, upStation, downStation, distance));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation) && Objects.equals(distance,
                                                                                  section.distance)
            && Objects.equals(line, section.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line);
    }
}
