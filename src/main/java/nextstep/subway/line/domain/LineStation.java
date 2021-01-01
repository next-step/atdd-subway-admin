package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Getter
@EqualsAndHashCode(of = {"line", "section"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "line_station")
@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Section section;

    public LineStation(final Line line, final Section section) {
        this.line = Objects.requireNonNull(line);
        this.section = Objects.requireNonNull(section);
    }

    public Station getUpStation() {
        return section.getUpStation();
    }

    public Station getDownStation() {
        return section.getDownStation();
    }

    public boolean isUpStation(final Station station) {
        return section.isUpStation(station);
    }

    public boolean isDownStation(final Station station) {
        return section.isDownStation(station);
    }

    public boolean contains(final Station station) {
        return isUpStation(station) || isDownStation(station);
    }

    public boolean contains(final Long stationId) {
        return section.contains(stationId);
    }

    public boolean canSeparate(final Section other) {
        return section.canSeparate(other);
    }

    public void update(final Section other) {
        section.separate(other);
    }

    public void merge(final LineStation other) {
        section.merge(other.section);
    }
}
