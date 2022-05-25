package nextstep.subway;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public final class SectionUtils {

    private SectionUtils() {
    }

    public static Section generateSection(String upStationName, String downStationName, long distance) {
        Station upStation = new Station(upStationName);
        Station downStation = new Station(downStationName);
        return new Section(upStation, downStation, distance);
    }
}
