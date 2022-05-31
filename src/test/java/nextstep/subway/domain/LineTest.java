package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private final static Station station1 = new Station(1L, "강남역");
    private final static Station station2 = new Station(2L, "삼성역");
    private final static Station station3 = new Station(3L, "잠실역");
    private final static Section section1 = new Section(1L, station1, station2, 5);
    private final static Section section2 = new Section(2L, station2, station3, 5);

    @Test
    @DisplayName("노선은 구간 정보를 저장한다")
    void addSection() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.addSection(section1);
        List<Station> actual = line.getLineStations();

        // then
        assertThat(actual).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("노선은 역 정보 출력시 상행종착역 부터 하행종착역 순서대로 반환한다")
    void getLineStations() {
        // given
        Line line = new Line("2호선", "green");
        line.addSection(section1);
        line.addSection(section2);

        // when
        List<Station> actual = line.getLineStations();

        // then
        assertThat(actual).containsExactly(station1, station2, station3);
    }
}
