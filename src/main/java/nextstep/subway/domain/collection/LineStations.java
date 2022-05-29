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

@Embeddable
public class LineStations {

    private static final int START = 0;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "line_id",foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public Set<Station> includeStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for(LineStation lineStation : lineStations){
            stations.add(lineStation.getUpStation());
            stations.add(lineStation.getDownStation());
        }
        return stations;
    }

    public void add(LineStation target) {
        lineStations.add(target);
    }

    public boolean addSection(Line line, Station upStation, Station downStation, long distance) {
        if (addBetween(line, upStation, downStation, distance)) {
            return true;
        }
        if (addStartSection(line, upStation, downStation, distance)) {
            return true;
        }
        return addEndSection(line, upStation, downStation, distance);
    }

    private boolean addStartSection(Line line, Station upStation, Station downStation, long distance) {
        LineStation startStation = findUpStation(downStation);
        if(startStation != null){
            lineStations.add(START,new LineStation(line, upStation, downStation, distance));
            return true;
        }
        return false;
    }

    private boolean addEndSection(Line line, Station upStation, Station downStation, long distance) {
        LineStation endStation = findDownStation(upStation);
        if(endStation != null){
            lineStations.add(new LineStation(line, upStation, downStation, distance));
            return true;
        }
        return false;
    }

    private boolean addBetween(Line line, Station upStation, Station downStation, long distance) {
        LineStation up = findUpStation(upStation);
        LineStation down = findDownStation(downStation);
        validateAlreadySection(up, down);

        if (up != null) {
            long newDistance = up.getDistance() - distance;
            Station copyDownStation = up.getDownStation().copy();
            up.updateUpStation(downStation, distance);
            lineStations.add(new LineStation(line, downStation, copyDownStation, newDistance));
            return true;
        }
        if (down != null) {
            long newDistance = down.getDistance() - distance;
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
            throw new IllegalArgumentException("[ERROR] 이미 구간이 존재합니다.");
        }
    }

}
