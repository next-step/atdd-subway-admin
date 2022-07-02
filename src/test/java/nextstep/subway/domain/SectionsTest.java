package nextstep.subway.domain;

import nextstep.subway.exception.InvalidRemoveSectionException;
import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.NotFoundSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Sections 도메인 객체에 대한 테스트")
class SectionsTest {
    public static Sections 수락마들노원구간;

    @BeforeEach
    void setUp() {
        수락마들노원구간 = new Sections(new ArrayList<>(Arrays.asList(SectionTest.수락마들, SectionTest.마들노원)));
    }

    @DisplayName("구간목록에 포함된 지하철역을 조회할 수 있다.")
    @Test
    void getStations() {
        List<Station> stations = 수락마들노원구간.findStations();
        assertThat(stations).containsExactly(StationTest.수락산역, StationTest.마들역, StationTest.노원역);
    }

    @DisplayName("구간에 포함되지 않은 역은 삭제할 수 없다.")
    @Test
    void removeExceptionWithInvalidStation() {
        System.out.println(수락마들노원구간.toString());
        assertThatThrownBy(() -> 수락마들노원구간.remove(StationTest.도봉산역)).isExactlyInstanceOf(NotFoundSectionException.class);
    }

    @DisplayName("구간이 1개 이하이면 구간을 삭제할 수 없다.")
    @Test
    void removeExceptionWithMinimum() {
        Sections sections = new Sections(Collections.singletonList(SectionTest.수락마들));
        assertThatThrownBy(() -> sections.remove(StationTest.수락산역)).isExactlyInstanceOf(InvalidRemoveSectionException.class);
    }

    @DisplayName("구간을 삭제할 수 있다.")
    @Test
    void remove() {
        수락마들노원구간.remove(StationTest.수락산역);
        assertAll(
            () -> assertThat(수락마들노원구간.getSections().size()).isEqualTo(1),
            () -> assertThat(수락마들노원구간.findStations()).containsExactly(StationTest.마들역, StationTest.노원역)
        );
    }

    @DisplayName("노선 역 목록에 일치하는 역이 없으면 구간을 추가할 수 없다.")
    @Test
    void addExceptionWithInvalidStation() {
        assertThatThrownBy(() -> 수락마들노원구간.add(SectionTest.중계하계)).isExactlyInstanceOf(InvalidSectionException.class);
    }

    @DisplayName("노선에 구간을 추가할 수 있다.")
    @Test
    void add() {
        수락마들노원구간.add(SectionTest.도봉수락);

        assertAll(
            () -> assertThat(수락마들노원구간.getSections().size()).isEqualTo(3),
            () -> assertThat(수락마들노원구간.findStations()).containsExactlyInAnyOrder(StationTest.수락산역, StationTest.마들역, StationTest.노원역, StationTest.도봉산역)
        );
    }
}
