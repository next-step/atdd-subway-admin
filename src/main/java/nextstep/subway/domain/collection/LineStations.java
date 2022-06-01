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
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.CreateSectionException;
import nextstep.subway.dto.line.SectionDTO;
import nextstep.subway.exception.DeleteSectionException;

@Embeddable
public class LineStations {

    private static final int CANT_DELETE_SIZE = 1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<LineStation> lineStations = new ArrayList<>();

    public void add(LineStation target) {
        lineStations.add(target);
    }

    public void addSection(SectionDTO sectionDTO) {
        boolean isExistSectionByUpSation = isExistSectionByUpSation(sectionDTO.getUpStation());
        boolean isExistSectionByDownSataion = isExistSectionByDownSataion(sectionDTO.getDownStation());
        validateAlreadySection(isExistSectionByUpSation, isExistSectionByDownSataion);

        if (isExistSectionByUpSation) {
            addBetweenSectionByUpStation(sectionDTO);
        }
        if (isExistSectionByDownSataion) {
            addBetweenSectionByDownStation(sectionDTO);
        }
        if (!isExistSectionByUpSation && !isExistSectionByDownSataion) {
            addStartOrEndSection(sectionDTO);
        }
    }

    private void addBetweenSectionByUpStation(SectionDTO sectionDTO) {
        LineStation sectionByUpStation = findSectionByUpStation(sectionDTO.getUpStation());

        long newDistance = sectionByUpStation.calcNewSectionDistance(sectionDTO.getDistance());
        Station linkDownStation = sectionByUpStation.getDownStation().copy();
        sectionByUpStation.updateDownStation(sectionDTO.getDownStation(), sectionDTO.getDistance());
        lineStations.add(sectionDTO.toLineStationLinkByDownStation(linkDownStation, newDistance));
    }

    private void addBetweenSectionByDownStation(SectionDTO sectionDTO) {
        LineStation sectionByDownStation = findSectionByDownStation(sectionDTO.getDownStation());

        long newDistance = sectionByDownStation.calcNewSectionDistance(sectionDTO.getDistance());
        sectionByDownStation.updateDownStation(sectionDTO.getUpStation(), newDistance);
        lineStations.add(sectionDTO.toLineStation());
    }

    private void addStartOrEndSection(SectionDTO sectionDTO) {
        LineStation startStation = findSectionByUpStation(sectionDTO.getDownStation());
        LineStation endStation = findSectionByDownStation(sectionDTO.getUpStation());
        validateNotFound(startStation, endStation);
        lineStations.add(sectionDTO.toLineStation());
    }

    private LineStation findSectionByUpStation(Station upStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getUpStation().equals(upStation))
                .findFirst()
                .orElse(null);
    }

    private boolean isExistSectionByUpSation(Station upStation) {
        return lineStations.stream()
                .anyMatch(lineStation -> lineStation.getUpStation().equals(upStation));
    }

    private LineStation findSectionByDownStation(Station downStation) {
        return lineStations.stream()
                .filter(lineStation -> lineStation.getDownStation().equals(downStation))
                .findFirst()
                .orElse(null);
    }

    private boolean isExistSectionByDownSataion(Station downStation) {
        return lineStations.stream()
                .anyMatch(lineStation -> lineStation.getDownStation().equals(downStation));
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

    public void delete(Station station) {
        boolean isExistSectionByUpSation = isExistSectionByUpSation(station);
        boolean isExistSectionByDownSation = isExistSectionByDownSataion(station);
        validateOnlyOneSection();
        validateDeleteSectionNotFound(isExistSectionByUpSation, isExistSectionByDownSation);

        LineStation sectionByUpStation = findSectionByUpStation(station);
        LineStation sectionByDownStation = findSectionByDownStation(station);
        if (!isExistSectionByDownSation) {
            lineStations.remove(sectionByUpStation);
        }
        if (!isExistSectionByUpSation) {
            lineStations.remove(sectionByDownStation);
        }
        if (isExistSectionByUpSation && isExistSectionByDownSation) {
            sectionByUpStation.merge(sectionByDownStation);
            lineStations.remove(sectionByDownStation);
        }
    }

    private void validateAlreadySection(boolean isExistSectionByUpSation, boolean isExistSectionByDownSataion) {
        if (isExistSectionByUpSation && isExistSectionByDownSataion) {
            throw new CreateSectionException("[ERROR] 이미 구간이 존재합니다.");
        }
    }

    private void validateDeleteSectionNotFound(boolean isExistSectionByUpSation, boolean isExistSectionByDownSation) {
        if (!isExistSectionByUpSation && !isExistSectionByDownSation) {
            throw new DeleteSectionException("[ERROR] 삭제할 구간을 찾을 수 없습니다.");
        }
    }

    private void validateNotFound(LineStation startStation, LineStation endStation) {
        if (!isStartOrEndStation(startStation, endStation)) {
            throw new CreateSectionException("[ERROR] 등록할 구간을 찾을 수 없습니다.");
        }
    }

    private void validateOnlyOneSection() {
        if(lineStations.size() == CANT_DELETE_SIZE){
            throw new DeleteSectionException("[ERROR] 구간이 하나인 경우 삭제할 수 없습니다.");
        }
    }

    private boolean isStartOrEndStation(LineStation startStation, LineStation endStation) {
        return startStation != null || endStation != null;
    }

    private boolean isExistSection(LineStation lineStation) {
        return lineStation != null;
    }
}
