package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineTest {

    @Test
    @DisplayName("노선은 이름, 색깔, 상행종점, 하행종점을 받아 생성한다")
    void 생성() {
        // given
        String name = "8호선";
        String color = "분홍색";
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Integer distance = 10;

        // when
        Line line = Line.of(name, color, upStation, downStation, distance);

        // then
        assertThat(line).isNotNull();
    }

    @Test
    @DisplayName("노선의 생성 시 이름, 색깔이 비어있으면 예외 발생")
    void 생성시_필수값_예외() {
        // given
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");

        // expect
        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.of("이름", null, upStation, downStation, 10)),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> Line.of(null, "색깔", upStation, downStation, 10))
        );
    }

    @Test
    @DisplayName("노선은 이름, 색깔을 받아 업데이트 한다")
    void 업데이트() {
        // given
        String name = "8호선";
        String color = "분홍색";
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Integer distance = 10;
        Line line = Line.of(name, color, upStation, downStation, 10);

        // when
        String newName = "1호선";
        String newColor = "파랑색";
        line.update(newName, newColor);

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(newName),
                () -> assertThat(line.getColor()).isEqualTo(newColor)
        );
    }

    @Test
    @DisplayName("잠실역, 장지역 구간 사이에 문정역 구간을 추가한다")
    void addSection() {
        // given
        String name = "8호선";
        String color = "분홍색";
        Station upStation = new Station("잠실역");
        Station downStation = new Station("장지역");
        Integer distance = 10;
        Line line = Line.of(name, color, upStation, downStation, distance);

        // given
        Station addStation = new Station("문정역");
        Integer addDistance = 10;
        Section section = Section.of(upStation, addStation, addDistance, line);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections().size()).isEqualTo(2);
    }
}
