package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Table(uniqueConstraints={@UniqueConstraint(columnNames={"line_id","station_id"})})
@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_line_station_to_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station station;

    /**
     * 생성자
     */
    protected LineStation() {}

    private LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }

    /**
     * 생성 메소드
     */
    public static LineStation create(Line line, Station station) {
        validate(line, station);
        return new LineStation(line, station);
    }

    private static void validate(Line line, Station station) {
        if (Objects.isNull(line)) {
            throw new NullPointerException("노선이 존재하지 않습니다.");
        }

        if (Objects.isNull(station)) {
            throw new NullPointerException("역이 존재하지 않습니다.");
        }
    }

    /**
     * 연관관계 메소드
     */
    public void setStation(Station station) {
        this.station = station;
        this.line.addLineStation(this);
    }

    /**
     * 기타 메소드
     */
    public Line line() {
        return line;
    }

    public Station station() {
        return station;
    }
}
