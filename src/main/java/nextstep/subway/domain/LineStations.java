package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import nextstep.subway.exception.AllRegisteredStationsException;
import nextstep.subway.exception.NotAllIncludedStationsException;
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
        Optional<LineStation> preLineStation = findByPreStationId(null);

        List<LineStation> list = new ArrayList<>();
        while (preLineStation.isPresent()) {
            LineStation lineStation = preLineStation.get();
            list.add(lineStation);
            preLineStation = findByPreStationId(lineStation.getDownLineStation().getId());
        }
        return list;
    }

    public void add(LineStation newLineStation) {
        validate(newLineStation.getUpLineStationId(), newLineStation.getDownLineStation().getId());
        findStation(newLineStation.getDownLineStation().getId())
            .ifPresent(ls -> ls.updateFirstNode(newLineStation.getUpLineStationId()));
        findByPreStationId(newLineStation.getUpLineStationId())
            .ifPresent(ls -> {
                validateDistance(newLineStation.getDistance(), ls.getDistance());
                ls.updatePreStationAndDistance(newLineStation.getDownLineStation().getId(),
                    newLineStation.getDistance());
            });
        lineStations.add(newLineStation);
    }

    private void validate(Long upStationId, Long downStationId) {
        boolean isPresentUpStation = findStation(upStationId).isPresent();
        boolean isPresentDownStation = findStation(downStationId).isPresent();

        if (isPresentUpStation && isPresentDownStation) {
            throw new AllRegisteredStationsException();
        }

        if (!isPresentDownStation && !isPresentUpStation) {
            throw new NotAllIncludedStationsException();
        }
    }

    private void validateDistance(Integer newDistance, Integer nowDistance) {
        if (nowDistance <= newDistance) {
            throw new ShortDistanceException(nowDistance, newDistance);
        }
    }

    private Optional<LineStation> findByPreStationId(Long id) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.getUpLineStationId() == id)
            .findFirst();
    }

    private Optional<LineStation> findStation(Long id) {
        return lineStations.stream()
            .filter(lineStation -> lineStation.getDownLineStation().getId() == id)
            .findFirst();
    }

}
