package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private static final int DISTANCE = 5;

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 광교역;
    private Distance distance;
    private Section 강남역_판교역_구간;
    private Section 정자역_광교역_구간;

    @BeforeEach
    void setUp() {
        강남역 = Station.of(2L, "강남역");
        판교역 = Station.of(1L, "판교역");
        정자역 = Station.of(3L, "정자역");
        광교역 = Station.of(4L, "광교역");
        distance = Distance.from(DISTANCE);
        강남역_판교역_구간 = Section.of(강남역, 판교역, distance);
        정자역_광교역_구간 = Section.of(정자역, 광교역, distance);
    }

    @DisplayName("지하철역 구간 Section을 목록으로 Sections를 생성한다.")
    @Test
    void generate01() {
        assertThatNoException().isThrownBy(() ->
            Sections.from(Arrays.asList(강남역_판교역_구간, 정자역_광교역_구간))
        );
    }
}