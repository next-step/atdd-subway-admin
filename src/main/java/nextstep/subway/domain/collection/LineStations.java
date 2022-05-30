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
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CreateSectionException;
import nextstep.subway.vo.SectionVO;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "line_id",foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation target) {
        lineStations.add(target);
    }

    public boolean addSection(SectionVO sectionVO) {
        if (addBetweenSection(sectionVO)) {
            return true;
        }
        return addStartOrEndSection(sectionVO);
    }

    private boolean addStartOrEndSection(SectionVO sectionVO) {
        LineStation startStation = findSectionByUpStation(sectionVO.getDownStation());
        LineStation endStation = findSectionByDownStation(sectionVO.getUpStation());
        if(isStartOrEndStation(startStation, endStation)){
            lineStations.add(new LineStation(sectionVO.getLine(), sectionVO.getUpStation(), sectionVO.getDownStation(), sectionVO.getDistance()));
            return true;
        }
        return false;
    }

    private boolean addBetweenSection(SectionVO sectionVO) {
        LineStation sectionByUpStation = findSectionByUpStation(sectionVO.getUpStation());
        LineStation sectionByDownStation = findSectionByDownStation(sectionVO.getDownStation());
        validateAlreadySection(sectionByUpStation, sectionByDownStation);

        if (isExistSection(sectionByUpStation)){
            long newDistance = sectionByUpStation.calcNewSectionDistance(sectionVO.getDistance());
            Station copyDownStation = sectionByUpStation.getDownStation().copy();
            sectionByUpStation.updateDownStation(sectionVO.getDownStation(), sectionVO.getDistance());
            lineStations.add(new LineStation(sectionVO.getLine(),sectionVO.getDownStation(), copyDownStation, newDistance));
            return true;
        }
        if (isExistSection(sectionByDownStation)) {
            long newDistance = sectionByDownStation.calcNewSectionDistance(sectionVO.getDistance());
            sectionByDownStation.updateDownStation(sectionVO.getUpStation(), newDistance);
            lineStations.add(new LineStation(sectionVO.getLine(), sectionVO.getUpStation(), sectionVO.getDownStation(), sectionVO.getDistance()));
            return true;
        }
        return false;
    }

    private LineStation findSectionByUpStation(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation().equals(upStation))
                .findFirst()
                .orElse(null);
    }

    private LineStation findSectionByDownStation(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getDownStation().equals(downStation))
                .findFirst()
                .orElse(null);
    }

    private void validateAlreadySection(LineStation sectionByUpStation, LineStation sectionByDownStation) {
        if (isExistSection(sectionByUpStation) && isExistSection(sectionByDownStation)){
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
        LineStation downLineStation = findSectionByDownStation(cursor.getUpStation());
        if (downLineStation == null) {
            return cursor;
        }
        return findStartSection(downLineStation);
    }

    private void addOrderStations(Set<Station> orderStations, LineStation cursor) {
        orderStations.add(cursor.getUpStation());
        orderStations.add(cursor.getDownStation());
        LineStation upStation = findSectionByUpStation(cursor.getDownStation());
        if (isExistSection(upStation)) {
            addOrderStations(orderStations, upStation);
        }
    }

    private boolean isStartOrEndStation(LineStation startStation, LineStation endStation) {
        return startStation != null || endStation != null;
    }

    private boolean isExistSection(LineStation lineStation) {
        return lineStation != null;
    }
}
