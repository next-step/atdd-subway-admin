package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.AllRegisteredStationsException;
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

    public void add(LineStation newLineStation) {
        validate(newLineStation);
        findStation(newLineStation.getStation().getId())
            .ifPresent(ls -> ls.updateFirstNode(newLineStation.getPreStationId()));
        findPreStation(newLineStation.getPreStationId())
            .ifPresent(ls -> {
                validateDistance(newLineStation.getDistance(), ls.getDistance());
                ls.updatePreStationId(newLineStation.getStation().getId());
            });
        lineStations.add(newLineStation);
    }

    private void validate(LineStation newLineStation) {
        validateAllRegisteredStations(newLineStation.getPreStationId(), newLineStation.getStation().getId());
    }

    private void validateAllRegisteredStations(Long upStationId, Long downStationId) {
        if (findStation(upStationId).isPresent() && findStation(downStationId).isPresent()) {
            throw new AllRegisteredStationsException();
        }
    }

    private void validateDistance(Integer newDistance, Integer nowDistance) {
        if (nowDistance <= newDistance) {
            throw new ShortDistanceException(nowDistance, newDistance);
        }
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
