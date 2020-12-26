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

    @ManyToOne
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
}
