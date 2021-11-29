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
        Station station1 = stationRepository.save(new Station("강남역"));
        Station station2 = stationRepository.save(new Station("잠실역"));
        Line line = 지하철역_목록_포함된_노선_생성됨("2호선", "RED", station1, station2);

        // when
        LineStationCollection lineStationCollection = LineStationCollection.of(line,
            Arrays.asList(station1, station2));

        // then
        List<String> expectNames = Arrays.asList(station1.getName(), station2.getName());
        assertThat(lineStationCollection.getLineStationResponses()).extracting("name")
            .containsExactlyElementsOf(expectNames);
    }

    private Line 지하철역_목록_포함된_노선_생성됨(String lineName, String lineColor, Station station,
        Station nextStation) {
        Line line = lineRepository.save(new Line(lineName, lineColor));
        LineStation lineStation = LineStation.of(station.getId(), nextStation.getId(),
            Distance.of(1000));
        line.addLineStation(lineStation);
        return line;
    }
}
