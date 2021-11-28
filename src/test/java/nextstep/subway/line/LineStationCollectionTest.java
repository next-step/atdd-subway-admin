package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.StationSection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineStationCollectionTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    StationRepository stationRepository;

    @Test
    @DisplayName("Line,Station 정보를 가지는 일급컬렉션 맵핑 테스트, 노선이 가진 지하철역 이름을 모두 순서대로 포함하고 있어야함")
    void create() {
        // given
        StationSection stationSection = createStationSection("강남역", "잠실역");
        Line line = 지하철역_목록_포함된_노선_생성됨("2호선", "RED", stationSection);

        // when
        LineStationCollection lineStationCollection = LineStationCollection.of(line,
            Arrays.asList(stationSection.getStation(), stationSection.getNextStation()));

        // then
        List<String> expectNames = Arrays.asList(stationSection.getStation().getName(),
            stationSection.getNextStation().getName());
        assertThat(lineStationCollection.getLineStationResponses()).extracting("name")
            .containsExactlyElementsOf(expectNames);
    }

    private StationSection createStationSection(String stationName, String nextStationName) {
        Station station1 = stationRepository.save(new Station(stationName));
        Station station2 = stationRepository.save(new Station(nextStationName));
        return StationSection.of(station1, station2);
    }

    private Line 지하철역_목록_포함된_노선_생성됨(String lineName, String lineColor,
        StationSection stationSection) {
        Line line = lineRepository.save(new Line(lineName, lineColor));
        LineStation lineStation = LineStation.of(stationSection, Distance.of(1000));
        line.addLineStation(lineStation);
        return line;
    }
}
