package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("테스트 라인", "색상", 100L);
    }

    @Test
    @DisplayName("노선에 포함되는 지하철 객체는 null일 수 없음")
    void setStations() {
        assertThatThrownBy(() -> line.setStations(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Line.MESSAGE_STATION_SHOULD_NOT_EMPTY);
    }

    @Test
    @DisplayName("노선에 포함되는 지하철 객체는 식별자를 가져야 함")
    void setStations2() {
        Station upStation = new Station("테스트 상행역");
        Station downStation = new Station("테스트 하행역");
        assertThatThrownBy(() -> line.setStations(upStation, downStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Line.MESSAGE_STATION_SHOULD_HAS_ID);
    }

    @Test
    @DisplayName("노선에 포함되는 지하철은 서로 다른 역이어야 함")
    void setStations3() {
        Station upStation = new Station(1L,"테스트 상행역");
        assertThatThrownBy(() -> line.setStations(upStation, upStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(Line.MESSAGE_STATION_SHOULD_BE_DIFFERENT);
    }

    @Test
    @DisplayName("빈 값이 아닌 값으로 노선과 색상을 변경할 수 있음")
    void modifyLine1() {
        line.modifyLine("수정된 노선명","수정된 색상");

        assertThat(line).isEqualTo(new Line("수정된 노선명","수정된 색상",line.getDistance()));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"","  "})
    void modifyLine2(String wrongValue) {
        line.modifyLine(wrongValue,wrongValue);

        assertThat(line).isEqualTo(new Line("테스트 라인", "색상", 100L));
    }

    @Test
    @DisplayName("입력받은 역 들이 노선과 끝과 동일한지 확인히야 판단")
    void hasSameEndStations() {
        Station upStation = new Station(1L,"테스트 상행역");
        Station downStation = new Station(2L,"테스트 하행역");
        line.setStations(upStation,downStation);

        boolean hasSame = line.hasSameEndStations(upStation, downStation);

        assertTrue(hasSame);
    }

    @Test
    @DisplayName("입력받은 역 들이 노선의 상행/하행역과 반대로 입력되었다면 동일한 것으로 판단")
    void hasSameEndStations2() {
        Station upStation = new Station(1L,"테스트 상행역");
        Station downStation = new Station(2L,"테스트 하행역");
        line.setStations(upStation,downStation);

        boolean hasSame = line.hasSameEndStations(downStation, upStation);

        assertTrue(hasSame);
    }
}