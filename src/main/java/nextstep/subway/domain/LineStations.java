package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.ShortDistanceException;

@Embeddable
public class LineStations {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void init(Long upStationId, Long downStationId, Integer distance) {
        lineStations.add(new LineStation(new Station(upStationId), null, null));
        lineStations.add(new LineStation(new Station(downStationId), upStationId, distance));
    }

    public void remove() {
        lineStations.clear();
    }

    public List<LineStation> getStationsInOrder() {
        Optional<LineStation> optionalPreLineStation = findPreStation(null);

        List<LineStation> result = new ArrayList<>();
        while (optionalPreLineStation.isPresent()) {
            LineStation preLineStation = optionalPreLineStation.get();
            result.add(preLineStation);
            optionalPreLineStation = findPreStation(preLineStation.getStation().getId());
        }
        return result;
    }

    public void add(LineStation lineStation) {
        findStation(lineStation.getStation().getId())
            .ifPresent(ls -> ls.updateFirstNode(lineStation.getPreStationId()));
        findPreStation(lineStation.getPreStationId())
            .ifPresent(ls -> {
                if (ls.getDistance() <= lineStation.getDistance()) {
                    throw new ShortDistanceException(ls.getDistance(), lineStation.getDistance());
                }
                ls.updatePreStationId(lineStation.getStation().getId());
            });
        lineStations.add(lineStation);
    }

    private Optional<LineStation> findPreStation(Long id) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.getPreStationId() == id)
            .findFirst();
    }

    private Optional<LineStation> findStation(Long id) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.getStation().getId() == id)
            .findFirst();
    }

}
