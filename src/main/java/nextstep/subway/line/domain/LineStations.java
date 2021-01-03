package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.AlreadySavedLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.NotRegisteredStationException;
import nextstep.subway.station.exception.StationNotFoundException;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2020-12-30
 */
@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public List<LineStation> getLineStations() {
        Optional<LineStation> preLineStation = lineStations.stream()
                .filter(it -> it.getPreStation() == null)
                .findFirst();

        List<LineStation> result = new LinkedList<>();
        while (preLineStation.isPresent()) {
            LineStation preStation = preLineStation.get();
            result.add(preStation);
            preLineStation = lineStations.stream()
                    .filter(it -> it.getPreStation() == preStation.getStation())
                    .findFirst();
        }
        return result;
    }

    public void add(LineStation lineStation) {

        if (lineStations.isEmpty()) {
            lineStations.add(lineStation);
            return;
        }

        addSectionValidate(lineStation);

        if (contains(lineStation.getPreStation())) {
            this.lineStations.stream()
                    .filter(it -> it.getPreStation() == lineStation.getPreStation())
                    .findFirst()
                    .ifPresent(it -> it.updatePreStation(lineStation.getStation(), lineStation.getDistance()));
            lineStations.add(lineStation);
            return;
        }

        if (contains(lineStation.getStation())) {
            this.lineStations.stream()
                    .filter(it -> it.getStation() == lineStation.getStation())
                    .findFirst()
                    .ifPresent(it -> it.updateStation(lineStation.getPreStation(), lineStation.getDistance()));
            lineStations.add(lineStation);
        }
    }

    private boolean contains(Station station) {
        return this.lineStations.stream()
                .anyMatch(lineStation -> lineStation.getStation() == station);
    }

    private void addSectionValidate(LineStation lineStation) {
        boolean upStationExist = contains(lineStation.getPreStation());
        boolean downStationExist = contains(lineStation.getStation());
        if (upStationExist && downStationExist) {
            throw new AlreadySavedLineException("이미 등록된 구간합니다.");
        }

        if (!upStationExist && !downStationExist) {
            throw new NotRegisteredStationException("등록할 수 없는 구간입니다.");
        }
    }

}
