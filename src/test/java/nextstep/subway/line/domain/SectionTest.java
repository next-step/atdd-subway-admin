package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    @Test
    @DisplayName("Section 클래스의 getStations 메서드의 반환값(List)에 add가 되는지 검증")
    public void validGetStationsInSectionClass() throws Exception {
        //given
        Line line = new Line("2호선", "green");
        Station 강남 = new Station("강남");
        Station 역삼 = new Station("역삼");
        Section section = new Section(line, 강남, 역삼, 10L);

        //when then
        List<Station> stations = section.getStations();

        //then
        Station 잠실 = new Station("잠실");
        stations.add(잠실);
        assertThat(stations).hasSize(3);
    }
}
