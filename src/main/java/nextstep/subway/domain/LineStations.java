package nextstep.subway.domain;

import nextstep.subway.exception.ElementNotFoundException;
import nextstep.subway.exception.InvalidParameterException;

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
        LineStation existedStation = getTargetExistedStation(infixLineStation);

        if (existedStation == null) {
            add(infixLineStation);
            return;
        }
        checkValidationParameter(existedStation, infixLineStation);

        if (existedStation.isEqualsId(infixLineStation.getStation().getId())) {
            infixLineStation.resetStation(infixLineStation.getPreStation());
            infixLineStation.resetPreStation(existedStation.getPreStation());
        }
        existedStation.resetPreStation(infixLineStation.getStation());

        add(infixLineStation);
    }

    public List<LineStation> getList() {
        return lineStations;
    }

    public List<LineStation> getSortList() {
        List<LineStation> sortList = new ArrayList<>();

        LineStation upStation = getUpLineStation();
        sortList.add(upStation);

        for (int i = 1; i < lineStations.size(); i++) {
            LineStation finalUpStation = upStation;

            LineStation station = lineStations
                    .stream()
                    .filter(lineStation
                            -> lineStation.getPreStation() != null
                            && finalUpStation.isEqualsId(lineStation.getPreStation().getId()))
                    .findAny()
                    .orElseThrow(() -> new ElementNotFoundException());

            upStation = station;
            sortList.add(station);
        }

        return sortList;
    }

    private LineStation getNextStationByStationId(Long id) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getPreStation() != null)
                .filter(lineStation -> lineStation.getPreStation().getId().equals(id))
                .findAny().orElse(null);
    }

    private LineStation getUpLineStation() {
        return lineStations.stream()
                .filter(lineStations -> lineStations.getPreStation() == null)
                .findAny()
                .orElseThrow(() -> new ElementNotFoundException());
    }

    private LineStation getTargetExistedStation(LineStation infixLineStation) {
        LineStation existedStation = lineStations
                .stream()
                .filter(lineStation -> lineStation.isEqualsId(infixLineStation.getStation().getId()))
                .findAny()
                .orElse(lineStations
                        .stream()
                        .filter(lineStation -> lineStation.isEqualsId(infixLineStation.getPreStation().getId()))
                        .findAny()
                        .orElse(null));

        if (existedStation.isEqualsId(infixLineStation.getPreStation().getId())) {
            existedStation = getNextStationByStationId(existedStation.getStation().getId());
        }
        return existedStation;
    }

    private void checkValidationParameter(LineStation existedStation, LineStation infixLineStation) {
        if (existedStation.getStation().equals(infixLineStation.getPreStation())) {
            throw new InvalidParameterException("상행선과 하행선을 모두 동일하게 등록할 수 없습니다.");
        }

        if (existedStation.isGatherThanPrice(infixLineStation)) {
            throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
        }
    }

}
