package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(Station upStation, Station downStation, Long distance) {
        if (this.lineStations.size() == 0)  {
            lineStations.add(new LineStation(distance, upStation, downStation, true));
            return;
        }
        LineStation addTarget = findAddTarget(upStation, downStation);

        checkPossibleAddSection(addTarget, distance);

        this.lineStations.add(
                addTarget.addStation(upStation, downStation, distance)
        );
    }

    public List<Station> getStationsSortedByUpToDown() {
        List<Station> result = new ArrayList<>();
        LineStation lineStation = this.lineStations
                .stream()
                .filter(LineStation::isStart)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("노선의 시작 상/하행역 정보를 찾을 수 없습니다."));
        result.add(lineStation.getUpStation());
        result.add(lineStation.getDownStation());

        for (int i = 0; i < this.lineStations.size() - 1; i++) {
            final LineStation preLineStation = lineStation;
            lineStation = this.lineStations
                    .stream()
                    .filter(value -> value.getUpStation().isSameId(preLineStation.getDownStation().getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            preLineStation.getDownStation().getName() + " 의 다음 노선 정보를 찾을 수 없습니다."
                        ));
            result.add(lineStation.getDownStation());
        }

        return result;
    }

    private LineStation findAddTarget(Station upStation, Station downStation) {
        LineStation findByUpStation = findLineStationByUpStationId(upStation);
        LineStation findByDownStation = findLineStationByDownStationId(downStation);

        if (findByUpStation != null && findByDownStation != null) {
            throw new IllegalArgumentException("기존에 등록된 상/하행역을 등록할 수 없습니다.");
        }
        if (findByUpStation == null && findByDownStation == null) {
            throw new IllegalArgumentException("기존 노선에 등록되지 않은 상/하행역을 추가할 수 없습니다.");
        }

        if (findByUpStation != null) {
            return findByUpStation;
        }
        return findByDownStation;
    }

    private void checkPossibleAddSection(LineStation addTarget, Long distance) {
        if (addTarget.getDistance() <= distance) {
            throw new IllegalArgumentException("기존 노선의 길이와 같거나 긴 노선을 추가할 수 없습니다.");
        }
    }

    private LineStation findLineStationByUpStationId(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameUpStation(upStation) || lineStation.isAddNewLast(upStation))
                .findFirst()
                .orElse(null);
    }

    private LineStation findLineStationByDownStationId(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.isSameDownStation(downStation) || lineStation.isAddNewFirst(downStation))
                .findFirst()
                .orElse(null);
    }
}
