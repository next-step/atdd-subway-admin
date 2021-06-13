package nextstep.subway.enums;

import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;

import java.util.List;

public enum SectionAddType {
    NEW_UP, NEW_BETWEEN, NEW_DOWN;

    public static SectionAddType calcAddType(List<LineStation> lineStations, LineStation lineStation) {
        if (lineStations.get(0).isSameStation(lineStation.getStation())) {
            return NEW_UP;
        }
        if (lineStations.get(lineStations.size() - 1).isSameStation(lineStation.getPreStation())) {
            return NEW_DOWN;
        }
        return NEW_BETWEEN;
    }

    public static Section createSection(LineStation lineStation) {
        return Section.of(lineStation);
    }
}
