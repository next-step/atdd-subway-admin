package nextstep.subway.testutils;

import nextstep.subway.line.LineTestMethods;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationTestMethods;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

public class FactoryMethods {
    public static Station createStation(String name) {
        return Station.from(name);
    }

    public static Section createSection(Station upStation, Station downStation, int distance) {
        return Section.of(upStation, downStation, distance);
    }

    public static LineResponse createLineResponse
            (String name, String color, Long upStationId, Long downStationId, int distance) {
        return LineTestMethods.노선_생성(name, color, upStationId, downStationId, distance).as(LineResponse.class);
    }

    public static StationResponse createStationResponse(String stationName) {
        return StationTestMethods.지하철역_생성(stationName).as(StationResponse.class);
    }
}
