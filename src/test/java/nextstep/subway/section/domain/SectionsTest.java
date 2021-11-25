package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                sections::extractStationsWithOrdering
            )
            .withMessage("상행 종착역은 한 개가 있어야 합니다.");

    }

    @Test
    @DisplayName("구역이 하나인 경우, 추출되는 역 2개가 상행역부터 하행역 순으로 정렬되어야 한다")
    void 구역에서_역_추출() {
        // given
        Sections sections = line.getSections();

        // when
        List<Station> result = sections.extractStationsWithOrdering();

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
        sections.connect(Section.of(line, 2L, 3L, 2));
        sections.connect(Section.of(line, 3L, 4L, 2));
        sections.connect(Section.of(line, 4L, 6L, 2));
        sections.connect(Section.of(line, 6L, 5L, 2));

        // when
        List<Station> result = sections.extractStationsWithOrdering();

        // then
        assertAll(
            () -> assertThat(result).hasSize(6),
            () -> assertThat(result).containsExactly(
                Station.of(1L), Station.of(2L), Station.of(3L),
                Station.of(4L), Station.of(6L), Station.of(5L)
            )
        );
    }

    @Test
    @DisplayName("구간 중간에 새로운 구간이 추가되는 경우, 기존 구간의 상행역과 거리가 수정된다.")
    void 구간_중간에_새_구간_추가() {
        // given
        Sections sections = line.getSections();

        // when
        sections.connect(Section.of(line, 1L, 3L, 3));

        // then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(2),
            () -> assertThat(sections.getSections().get(0).getUpStation().equals(Station.of(3L))),
            () -> assertThat(sections.getSections().get(0).getDownStation().equals(Station.of(2L))),
            () -> assertThat(sections.getSections().get(0).getDistance().equals(Distance.from(7))),
            () -> assertThat(sections.getSections().get(1).getUpStation().equals(Station.of(1L))),
            () -> assertThat(sections.getSections().get(1).getDownStation().equals(Station.of(3L))),
            () -> assertThat(sections.getSections().get(1).getDistance().equals(Distance.from(3)))
        );
    }

    @Test
    @DisplayName("상행 종점에 새로운 구간이 추가되는 경우 추가만 된다")
    void 상행_종점에_새_구간_추가() {
        // given
        Sections sections = line.getSections();

        // when
        sections.connect(Section.of(line, 3L, 1L, 3));

        // then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(2),
            () -> assertThat(sections.getSections().get(0).getUpStation().equals(Station.of(1L))),
            () -> assertThat(sections.getSections().get(0).getDownStation().equals(Station.of(2L))),
            () -> assertThat(sections.getSections().get(0).getDistance().equals(Distance.from(10))),
            () -> assertThat(sections.getSections().get(1).getUpStation().equals(Station.of(3L))),
            () -> assertThat(sections.getSections().get(1).getDownStation().equals(Station.of(1L))),
            () -> assertThat(sections.getSections().get(1).getDistance().equals(Distance.from(3)))
        );
    }

    @Test
    @DisplayName("하행 종점에 새로운 구간이 추가되는 경우 추가만 된다")
    void 하행_종점에_새_구간_추가() {
        // given
        Sections sections = line.getSections();

        // when
        sections.connect(Section.of(line, 2L, 3L, 3));

        // then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(2),
            () -> assertThat(sections.getSections().get(0).getUpStation().equals(Station.of(1L))),
            () -> assertThat(sections.getSections().get(0).getDownStation().equals(Station.of(2L))),
            () -> assertThat(sections.getSections().get(0).getDistance().equals(Distance.from(10))),
            () -> assertThat(sections.getSections().get(1).getUpStation().equals(Station.of(2L))),
            () -> assertThat(sections.getSections().get(1).getDownStation().equals(Station.of(3L))),
            () -> assertThat(sections.getSections().get(1).getDistance().equals(Distance.from(3)))
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 예외 발생")
    void 예외_케이스1() {
        // given
        Sections sections = line.getSections();

        // when, then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.connect(Section.of(line, 1L, 3L, 10))
            )
            .withMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 합니다");
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록된 경우 예외 발생")
    void 예외_케이스2() {
        // given
        Sections sections = line.getSections();

        // when, then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.connect(Section.of(line, 1L, 2L, 5))
            )
            .withMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다");
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않은 경우 예외 발생")
    void 예외_케이스3() {
        // given
        Sections sections = line.getSections();

        // when, then
        assertThatIllegalArgumentException().isThrownBy(
                () -> sections.connect(Section.of(line, 3L, 4L, 5))
            )
            .withMessage("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
    }
}