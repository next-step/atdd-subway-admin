package nextstep.subway.section.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Getter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_section_station_info", columnNames={"line_id", "up_station_id", "down_station_id"}))
public class Section extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    private int distance;

    @Builder
    public Section(final Long id, final Station upStation, final Station downStation, final int distance) {
        this.id = id;
        this.registerStation(upStation, downStation, distance);
    }

    public void registerStation(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void registerLine(Line line) {
        Optional.ofNullable(line).ifPresent(l -> {
            this.line = line;
            l.appendSection(this);
        });
    }

    protected boolean isBefore(Section section) {
        return Objects.equals(downStation, section.getUpStation());
    }

    protected boolean isAfter(Section section) {
        return Objects.equals(upStation, section.getDownStation());
    }

    protected boolean contains(Station station) {
        return Objects.equals(upStation, station) || Objects.equals(downStation, station);
    }

    protected boolean hasSameUpStation(Section section) {
        return Objects.equals(upStation, section.getUpStation());
    }

    protected boolean hasSameDownStation(Section section) {
        return Objects.equals(downStation, section.getDownStation());
    }

    protected void overrideUpStation(final Section section) {
        updateDistance(section);

        this.upStation = section.downStation;
    }

    protected void overrideDownStation(final Section section) {
        updateDistance(section);

        this.downStation = section.upStation;
    }

    private void updateDistance(final Section section) {
        int distance = this.distance - section.getDistance();
        if (distance < 1) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }

        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", upStation, downStation, distance);
    }
}