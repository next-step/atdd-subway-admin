package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선")
class LineTest {
    @Test
    @DisplayName("노선 이름과 색상을 수정할 수 있다.")
    void 노선_수정() {
        Station upStation = new Station();
        Station downStation = new Station();

        Line line = Line.of("분당선", "bg-red-600", upStation, downStation, 10);
        line.update("다른분당선", "bg-red-200");

        assertAll(() -> assertThat(line.getName()).isEqualTo("다른분당선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-red-200"));
    }

    @Test
    @DisplayName("노선에서 역을 삭제할 수 있다.")
    void 역_삭제() {
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        Section 역삼_선릉_구간 = Section.of(역삼역, 선릉역, 6);

        Line 노선 = Line.of("분당선", "bg-red-600", 강남역, 역삼역, 4);
        노선.addSection(역삼_선릉_구간);

        노선.deleteStation(선릉역);
        assertThat(노선.getStations()).containsExactlyInAnyOrder(강남역, 역삼역);
    }
}
