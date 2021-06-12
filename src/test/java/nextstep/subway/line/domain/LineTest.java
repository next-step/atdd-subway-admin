package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 잠실역;
    private Line line;

    @BeforeEach
    public void setUp() {
        // given
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        잠실역 = new Station("잠실역");
        line = new Line("2호선", "green", 강남역, 역삼역, 10L);
    }

    @Test
    @DisplayName("강남역과 역삼역 10미터 사이에 잠실역 5미터를 추가한다.")
    public void addSection() {
        // when
        line.addSection(강남역, 잠실역, 5L);

        // then
        assertThat(line.getStations()).containsExactly(Arrays.array(강남역, 잠실역, 역삼역));
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록 가능")
    void addUpStationsInSection() {
        // when
        line.addSection(잠실역, 강남역, 5L);

        // then
        assertThat(line.getStations()).containsExactly(Arrays.array(잠실역, 강남역, 역삼역));
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록 가능")
    public void addDownStationsInSection() {
        // when
        line.addSection(역삼역, 잠실역, 5L);

        // then
        assertThat(line.getStations()).containsExactly(Arrays.array(강남역, 역삼역, 잠실역));
    }
}