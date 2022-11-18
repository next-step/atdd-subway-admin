package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public void infixSection(LineStation infixLineStation) {
        Long preStationId = infixLineStation.getPreStation().getId();
        Long stationId = infixLineStation.getStation().getId();

        lineStations.stream().forEach(lineStation -> {
            Long id = lineStation.getStation().getId();
            checkValidLineStation(preStationId, stationId, id);
            if (preStationId.equals(id)) {
                getTargetLineStation(preStationId, infixLineStation.getPreStation())
                        .resetPreStation(infixLineStation.getStation());
                return;
            }
            if (stationId.equals(id)) {
                resetLineStationByDownStandard(lineStation, infixLineStation);
                return;
            }
        });
        add(infixLineStation);
    }

    private void checkValidLineStation(Long preStationId, Long stationId, Long id) {
        if (preStationId.equals(id) && stationId.equals(id)) {
            throw new RuntimeException();
        }
    }

    private void resetLineStationByDownStandard(LineStation lineStation, LineStation infixLineStation) {
        Station temp = lineStation.getPreStation();
        lineStation.resetPreStation(infixLineStation.getPreStation());
        infixLineStation.reverseStation();
        infixLineStation.resetPreStation(temp);
    }

    private LineStation getTargetLineStation(Long id, Station station) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getPreStation() != null)
                .filter(lineStation -> lineStation.getPreStation().getId().equals(id))
                .findFirst().orElse(new LineStation(station));
    }

    public List<LineStation> getList() {
        return lineStations;
    }

    public List<LineStation> getSortList() {
        List<LineStation> sortList = new ArrayList<>();

        LineStation upStation = getPreLineStation();
        sortList.add(upStation);

        for (int i = 1; i < lineStations.size(); i++) {
            for (LineStation lineStation : lineStations) {
                if (lineStation.getPreStation() == null) {
                    continue;
                }
                if (upStation.getStation().getId().equals(lineStation.getPreStation().getId())) {
                    upStation = lineStation;
                    sortList.add(lineStation);
                }
            }
        }

        return sortList;
    }

    private LineStation getPreLineStation() {
        return lineStations.stream().filter(lineStations -> lineStations.getPreStation() == null).findFirst().get();
    }

}
