package nextstep.subway.domain;

import java.util.Objects;
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
import nextstep.subway.exception.NotFoundException;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(SectionBuilder sectionBuilder) {
        this.upStation = sectionBuilder.upStation;
        this.downStation = sectionBuilder.downStation;
        this.distance = sectionBuilder.distance;
    }

    public static SectionBuilder builder(Station upStation, Station downStation, Distance distance) {
        return new SectionBuilder(upStation, downStation, distance);
    }

    public static class SectionBuilder {
        private Long id;
        private final Station upStation;
        private final Station downStation;
        private final Distance distance;

        private SectionBuilder(Station upStation, Station downStation, Distance distance) {
            validateUpStationNotNull(upStation);
            validateDownStationNotNull(downStation);
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public SectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        private void validateUpStationNotNull(Station upStation) {
            if (Objects.isNull(upStation)) {
                throw new NotFoundException("상행역 정보가 없습니다.");
            }
        }

        private void validateDownStationNotNull(Station downStation) {
            if (Objects.isNull(downStation)) {
                throw new NotFoundException("하행역 정보가 없습니다.");
            }
        }
        public Section build() {
            return new Section(this);
        }
    }

    public void addLine(Line line) {
        validateLineNotNull(line);
        this.line = line;
    }

    private void validateLineNotNull(Line line) {
        if (Objects.isNull(line)) {
            throw new NotFoundException("노선 정보가 없습니다.");
        }
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Distance distance() {
        return distance;
    }
}
