package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    @DisplayName("중복 없이 역 ID 목록을 가져올 수 있다.")
    @Test
    void getStationsTest() {
        Long station1Id = 1L;
        Long station2Id = 2L;
        Long station3Id = 3L;
        int expectedSize = 3;

        Sections sections = new Sections();
        sections.initFirstSection(new Section(1L, station1Id, station2Id, 3L));
        sections.initFirstSection(new Section(2L, station2Id, station3Id, 5L));

        List<Long> stationIds = sections.getStationIdsWithoutDup();

        assertThat(stationIds).hasSize(expectedSize);
    }

    @DisplayName("초기화되지 않은 Sections에 Section 추가 시 예외 발생")
    @Test
    void addFailTest() {
        Sections sections = new Sections();

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 2L, 3L)))
                .isInstanceOf(InvalidSectionsActionException.class);
    }

    // TODO: 제시된 Section과 연결 가능한 구간이 있는지 확인하는 테스트 추가

    @DisplayName("상행 종점역 구간을 찾아낼 수 있다.")
    @Test
    void findEndUpSectionTest() {
        Section endUpStation = new Section(1L, 2L, 10L);

        Sections sections = new Sections(new ArrayList<>(Arrays.asList(
                endUpStation,
                new Section(2L, 3L, 10L),
                new Section (3L, 4L, 10L)
        )));

        assertThat(sections.findEndUpSection()).isEqualTo(endUpStation);
    }

    // TODO: 하행 종점역 구간을 찾아낼 수 있다.
}