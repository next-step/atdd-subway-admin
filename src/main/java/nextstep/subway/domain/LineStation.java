package nextstep.subway.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LineStation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "LINE_ID",foreignKey = @ForeignKey(name = "fk_line_station_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATION_ID",foreignKey = @ForeignKey(name = "fk_line_station_station"))
    private Station station;

    protected LineStation(){}

    public LineStation(Line line, Station station) {
        this.line = line;
        this.station = station;
    }


}
