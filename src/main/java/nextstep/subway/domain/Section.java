package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Column(name = "distance")
    private Long distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station lastUpStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station lastDownStation;

    protected Section() {
    }

    public Section(Line line, Long distance, Station lastUpStation, Station lastDownStation) {
        this.line = line;
        this.distance = distance;
        this.lastUpStation = lastUpStation;
        this.lastDownStation = lastDownStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(lastUpStation, lastDownStation);
    }
}
