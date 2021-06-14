package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {

    @Test
    @DisplayName("섹션 구간에 역을 추가하면 역사이즈가 3인지 확인한다")
    public void validGetStationsInSectionClass() throws Exception {
        // given
        Line line = new Line("2호선", "green");
        Station 강남 = new Station("강남");
        Station 역삼 = new Station("역삼");
        Section section = new Section(line, 강남, 역삼, 10L);

        // when
        List<Station> stations = section.getStations();

        // then
        Station 잠실 = new Station("잠실");
        stations.add(잠실);
        assertThat(stations).hasSize(3);
    }
}
