package nextstep.subway.domain.collection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CreateSectionException;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "line_id",foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation target) {
        lineStations.add(target);
    }

    public boolean addSection(Line line, Station upStation, Station downStation, long distance) {
        if (addBetweenSection(line, upStation, downStation, distance)) {
            return true;
        }
        return addStartOrEndSection(line, upStation, downStation, distance);
    }

    private boolean addStartOrEndSection(Line line, Station upStation, Station downStation, long distance) {
        LineStation startStation = findUpStation(downStation);
        LineStation endStation = findDownStation(upStation);
        if(startStation != null || endStation != null){
            lineStations.add(new LineStation(line, upStation, downStation, distance));
            return true;
        }
        return false;
    }

    private boolean addBetweenSection(Line line, Station upStation, Station downStation, long distance) {
        LineStation up = findUpStation(upStation);
        LineStation down = findDownStation(downStation);
        validateAlreadySection(up, down);

        if (up != null) {
            long newDistance = up.calcNewSectionDistance(distance);
            Station copyDownStation = up.getDownStation().copy();
            up.updateDownStation(downStation, distance);
            lineStations.add(new LineStation(line, downStation, copyDownStation, newDistance));
            return true;
        }
        if (down != null) {
            long newDistance = down.calcNewSectionDistance(distance);
            down.updateDownStation(upStation, newDistance);
            lineStations.add(new LineStation(line, upStation, downStation, distance));
            return true;
        }
        return false;
    }

    private LineStation findUpStation(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation().equals(upStation))
                .findFirst()
                .orElse(null);
    }

    private LineStation findDownStation(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getDownStation().equals(downStation))
                .findFirst().orElse(null);
    }

    private void validateAlreadySection(LineStation up, LineStation down) {
        if (up != null && down != null) {
            throw new CreateSectionException("[ERROR] 이미 구간이 존재합니다.");
        }
    }

    public Set<Station> orderStations() {
        Set<Station> orderStations = new LinkedHashSet<>();
        LineStation lineStation = lineStations.stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 구간이 존재하지 않습니다."));
        LineStation cursor = findStartSection(lineStation);
        addOrderStations(orderStations, cursor);
        return orderStations;
    }

    private LineStation findStartSection(LineStation cursor) {
        LineStation downLineStation = findDownStation(cursor.getUpStation());
        if (downLineStation == null) {
            return cursor;
        }
        return findStartSection(downLineStation);
    }

    private void addOrderStations(Set<Station> orderStations, LineStation cursor) {
        orderStations.add(cursor.getUpStation());
        orderStations.add(cursor.getDownStation());
        LineStation upStation = findUpStation(cursor.getDownStation());
        if (upStation != null) {
            addOrderStations(orderStations, upStation);
        }
    }
}
