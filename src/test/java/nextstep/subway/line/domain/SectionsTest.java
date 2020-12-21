package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        List<Long> stationIds = sections.getStationIds();

        assertThat(stationIds).hasSize(expectedSize);
    }

    @DisplayName("Sections 일급 컬렉션 내 Section이 하나도 없는 상태에서 추가 작업을 진행할 수 없다.")
    @Test
    void addFailWhenSectionsEmptyTest() {
        Sections sections = new Sections();

        assertThatThrownBy(() -> sections.addSection(new Section(1L, 2L, 3L)))
                .isInstanceOf(InvalidSectionsActionException.class);
    }

    @DisplayName("기존역 중 상행역과 일치하는 Section을 추가할 수 있다.")
    @Test
    void addWhenSectionSameWithUpStationTest() {

    }

    @DisplayName("기존역 중 하행역과 일치하는 Section을 추가할 수 있다.")
    @Test
    void addWhenSectionSameWithDownStationTest() {

    }
}