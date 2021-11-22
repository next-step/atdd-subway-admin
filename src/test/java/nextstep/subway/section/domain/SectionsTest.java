package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Line line;

    @BeforeEach
    private void setUp() {
        line = Line.from("신분당선", "bg-red-600", 1L, 2L, 10);
    }

    @Test
    @DisplayName("구역이 없을 때, 추출되는 역은 없다")
    void 구역이_빈_값일_때_역_추출() {
        // given
        Sections sections = Sections.empty();

        // when
        List<Station> result = sections.toStations();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("구역이 하나인 경우, 추출되는 역 2개가 상행역부터 하행역 순으로 정렬되어야 한다")
    void 구역에서_역_추출() {
        // given
        Sections sections = line.getSections();

        // when
        List<Station> result = sections.toStations();

        // then
        assertAll(
            () -> assertThat(result).hasSize(2),
            () -> assertThat(result).containsExactly(Station.of(1L), Station.of(2L))
        );
    }
}