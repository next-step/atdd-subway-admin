package nextstep.subway.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Line 지하철_2호선;
    private int 거리 = 10;
    private Station 강남역;
    private Station 역삼역;
    private Section 강남_역삼_지하철_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        강남_역삼_지하철_구간 = new Section(거리, 강남역, 역삼역);
        지하철_2호선 = new Line("2호선", "bg-green-600");
    }

    @Test
    @DisplayName("지하철 노선의 정보 변경 테스트")
    void updateLine() {
        지하철_2호선.update("2호선_연장", "bg-green-500");

        Assertions.assertAll(
                () -> assertThat(지하철_2호선.getColor()).isEqualTo("bg-green-500"),
                () -> assertThat(지하철_2호선.getName()).isEqualTo("2호선_연장")
        );
    }

    @Test
    @DisplayName("지하철 노선내 구간 등록 테스트")
    void addLineSection() {
        지하철_2호선.addSection(강남_역삼_지하철_구간);
        assertThat(지하철_2호선.getSections().getSections()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선 등록 후 조회시 정렬 테스트")
    void addStationSort() {
        Station 교대역 = new Station("교대역");
        Section 교대_강남_지하철_구간 = new Section(거리, 교대역, 강남역);

        지하철_2호선.addSection(강남_역삼_지하철_구간);
        지하철_2호선.addSection(교대_강남_지하철_구간);

        List<Station> stations = 지하철_2호선.sortByStation();

        Assertions.assertAll(
                () -> assertThat(stations).size().isEqualTo(3),
                () -> assertThat(stations).containsExactly(교대역, 강남역, 역삼역)
        );
    }
}
