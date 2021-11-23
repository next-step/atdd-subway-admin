package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
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
    @DisplayName("구역이 없는데, 역을 조회하는 경우 예외 발생")
    void 구역이_빈_값일_때_역_추출() {
        // given
        Sections sections = Sections.empty();

        // when
        // then
        assertThatIllegalStateException().isThrownBy(
                sections::extractStationsApplyOrderingUpStationToDownStation
            )
            .withMessage("상행 종착역은 한 개가 있어야 합니다.");

    }

    @Test
    @DisplayName("구역이 하나인 경우, 추출되는 역 2개가 상행역부터 하행역 순으로 정렬되어야 한다")
    void 구역에서_역_추출() {
        // given
        Sections sections = line.getSections();

        // when
        List<Station> result = sections.extractStationsApplyOrderingUpStationToDownStation();

        // then
        assertAll(
            () -> assertThat(result).hasSize(2),
            () -> assertThat(result).containsExactly(Station.of(1L), Station.of(2L))
        );
    }

    @Test
    @DisplayName("임의 순서의 구역이 5개인 경우, 추출되는 역 6개가 상행역부터 하행역 순으로 정렬되어야 한다")
    void 구역에서_역_추출2() {
        // given - 역을 연결하면 1 -> 2 -> 3- > 4 -> 6 -> 5
        Sections sections = line.getSections();
        sections.add(Section.of(line, 3L, 4L, 2));
        sections.add(Section.of(line, 2L, 3L, 2));
        sections.add(Section.of(line, 4L, 6L, 2));
        sections.add(Section.of(line, 6L, 5L, 2));

        // when
        List<Station> result = sections.extractStationsApplyOrderingUpStationToDownStation();

        // then
        assertAll(
            () -> assertThat(result).hasSize(6),
            () -> assertThat(result).containsExactly(
                Station.of(1L), Station.of(2L), Station.of(3L),
                Station.of(4L), Station.of(6L), Station.of(5L)
            )
        );
    }
}