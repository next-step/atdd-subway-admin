package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineStations {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public LineStations(final List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public static LineStations init(final LineStation lineStation) {
        List<LineStation> lineStations = new ArrayList<>();
        lineStations.add(lineStation);
        return new LineStations(lineStations);
    }

    public void add(final LineStation lineStation) {
        lineStations.add(lineStation);
    }

    public List<Station> getStations(final Station baseStation) {
        List<Station> order = new ArrayList<>();
        order.add(baseStation);

        List<LineStation> targets = new ArrayList<>(this.lineStations);
        Station next = baseStation;
        for (LineStation target : targets) {
            if (target.getUpStation().equals(next)) {
                Station downStation = target.getDownStation();
                order.add(downStation);
                next = downStation;
            }
        }
        return order;
    }
}
