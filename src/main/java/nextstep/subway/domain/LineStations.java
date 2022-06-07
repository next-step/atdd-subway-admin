package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Embeddable
public class LineStations {
    private static Logger logger = Logger.getLogger(LineStation.class.getName());

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(Station upStation, Station downStation, Distance distance) {
        if (this.lineStations.isEmpty())  {
            lineStations.add(new LineStation(distance, upStation, downStation, true));
            return;
        }
        LineStation addTarget = findAddTarget(upStation, downStation);

        this.lineStations.add(
                addTarget.addStation(upStation, downStation, distance)
        );
    }

    public void deleteLineStation(Station station) {
        if (this.lineStations.size() == 1) {
            throw new IllegalArgumentException("노선에 구간이 1개일 때 삭제할 수 없습니다.");
        }

        LineStation target = findLineStation(
                lineStation -> lineStation.isSameUpStation(station) || (lineStation.isLastSection() && lineStation.isSameDownStation(station)),
                station.getName() + " 에 해당하는 구간이 1개가 아닙니다."
        );

        if (target == null) {
            throw new IllegalArgumentException(station.getName() + " 에 해당하는 구간을 찾을 수 없습니다.");
        }

        LineStation nextLineStation = findNextLineStation(target.getDownStation());
        LineStation preLineStation = findPreLineStation(target.getUpStation());

        if (target.isStartSection()) {
            nextLineStation.changeToFirstSection();
        }
        if (target.isLastSection()) {
            preLineStation.changeToLastSection();
        }
        if (target.isMiddleSection()) {
            preLineStation.changeDownStation(target.getDownStation());
            preLineStation.addDistance(target.getDistance());
        }

        this.lineStations.removeIf(lineStation -> lineStation.equals(target));
    }

    public List<Station> getStationsSortedByUpToDown() {
        List<Station> result = new ArrayList<>();
        LineStation lineStation = findStartLineStation();

        result.add(lineStation.getUpStation());
        result.add(lineStation.getDownStation());

        for (int i = 0; i < this.lineStations.size() - 1; i++) {
            lineStation = findNextLineStation(lineStation.getDownStation());
            result.add(lineStation.getDownStation());
        }

        return result;
    }

    private LineStation findAddTarget(Station upStation, Station downStation) {
        LineStation findByUpStation = findLineStationByUpStationId(upStation);
        LineStation findByDownStation = findLineStationByDownStationId(downStation);

        if (findByUpStation != null && findByDownStation != null) {
            throw new IllegalArgumentException("기존에 등록된 같은 상/하행역을 등록할 수 없습니다.");
        }
        if (findByUpStation == null && findByDownStation == null) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역을 추가할 수 없습니다.");
        }

        if (findByUpStation != null) {
            return findByUpStation;
        }
        return findByDownStation;
    }

    private LineStation findLineStation(Predicate<LineStation> predicate, String duplicateFoundMessage) {
        List<LineStation> findResult = lineStations.stream()
                .filter(predicate)
                .collect(Collectors.toList());

        if (findResult.size() > 1) {
            logger.log(Level.SEVERE, duplicateFoundMessage);
        }

        return findResult.stream()
                .max(Comparator.comparing(LineStation::getCreatedDate))
                .orElse(null);
    }

    private LineStation findLineStationByUpStationId(Station upStation) {
        return findLineStation(
                lineStation -> lineStation.isSameUpStation(upStation) || lineStation.isAddNewLast(upStation),
                upStation.getName() + " 의 상행역인 구간이 1개가 아니거나 종착역 이후 구간이 존재합니다."
        );
    }

    private LineStation findLineStationByDownStationId(Station downStation) {
        return findLineStation(
                lineStation -> lineStation.isSameDownStation(downStation) || lineStation.isAddNewFirst(downStation),
                downStation.getName() + " 의 하행역인 구간이 1개가 아니거나 시작역 이전 구간이 존재합니다."
        );
    }

    private LineStation findStartLineStation() {
        LineStation firstLineStation = findLineStation(
                LineStation::isStartSection,
                "노선의 시작점이 1개가 아닙니다."
        );

        if (firstLineStation == null) {
            throw new IllegalStateException("노선의 시작점을 찾을 수 없습니다.");
        }

        return firstLineStation;
    }

    private LineStation findNextLineStation(Station nextStation) {
        return findLineStation(
                lineStation -> lineStation.isSameUpStation(nextStation),
                nextStation.getName() + " 의 다음역 정보가 1개가 아닙니다."
        );
    }

    private LineStation findPreLineStation(Station preStation) {
        return findLineStation(
                lineStation -> lineStation.isSameDownStation(preStation),
                preStation.getName() + " 의 이전역 정보가 1개가 아닙니다."
        );
    }
}
