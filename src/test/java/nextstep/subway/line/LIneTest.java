package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@DataJpaTest
public class LIneTest {
    @DisplayName("노선에 속한 구간 정보 정렬 확인")
    @Test
    void getSoredSections() {
        //given
        Line line = new Line("신분당선", "red");
        Station first = new Station("1번역");
        Station second = new Station("2번역");
        Station third = new Station("3번역");
        Station fourth = new Station("4번역");
        line.addSection(third, fourth, 5);
        line.addSection(second, third, 5);
        line.addSection(first, second, 5);
        List<Station> stations = line.getSortedStations();

        //when
        Station firstStation = stations.get(0);
        Station lastStation = stations.get(stations.size() - 1);

        //then
        assertThat(firstStation).isEqualTo(first);
        assertThat(lastStation).isEqualTo(fourth);
    }
}
