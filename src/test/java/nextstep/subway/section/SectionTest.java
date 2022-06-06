package nextstep.subway.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class SectionTest {
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 양재역 = new Station(2L, "양재역");
    private static final Station 청량리역 = new Station(3L, "청량리역");
    private static final Station 인천역 = new Station(4L, "인천역");

    private Section 구간_강남_양재_7L;
    private Section 구간_강남_인천_4L;

    @BeforeEach
    void setUp() {
        구간_강남_양재_7L = new Section(강남역, 양재역, 7L);
        구간_강남_인천_4L = new Section(강남역, 인천역, 4L);
    }

    @DisplayName("upStation 과 downStation , distance 정보를 가진다.")
    @Test
    void createTest() {
        assertThat(구간_강남_인천_4L).isEqualTo(new Section(강남역, 인천역, 4L));
    }

    @DisplayName("distance 값은 음수를 가질수 없다.")
    @Test
    void invalidCreatedDistanceTest() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Section(강남역, 양재역, -1L));
    }

    @DisplayName("downStationId or upStationId 는 optional 이지만 둘중 하나 값을 존재 해야 한다.")
    @Test
    void noHasStationInformationTest() {
        assertThatThrownBy(() -> new Section(null, null, 1L))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("upStation or downStation 둘중 하나도 기존값이랑 맞지 않으면 에러를 발생한다.")
    @Test
    void updatableTest() {
        assertThatThrownBy(() -> 구간_강남_양재_7L.updatable(new Section(청량리역, 인천역, 2L)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 distance 보다 새로운 section 이 크거나 같으면 에러를 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {4L, 5L})
    void invalidDistanceTest(final long input) {
        assertThatThrownBy(() -> 구간_강남_인천_4L.updatable(new Section(청량리역, 인천역, input)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 일치하고 역 사이에 새로운 역을 등록할 경우")
    @Test
    void updatableMiddleWhenMatchUpStation() {
        assertThat(구간_강남_양재_7L.updatable(구간_강남_인천_4L)).isEqualTo(구간_강남_양재_7L);
    }

    @DisplayName("새로운역을 상행 종점으로 동록할 경우")
    @Test
    void updatableStartPointTest() {
        assertThat(구간_강남_양재_7L.updatable(new Section(인천역, 강남역, 2L))).isEqualTo(new Section(인천역, 강남역, 2L));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 등록되어 있다면 추가할수 없음")
    @Test
    void 상행역_하행역이_같으면_에러를_발생한다() {
        assertThatThrownBy(() -> 구간_강남_인천_4L.updatable(new Section(강남역, 인천역, 3L)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 구간_강남_인천_4L.updatable(new Section(인천역, 강남역, 3L)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 정보를 변경할수 있다.")
    @Test
    void updateStationTest() {
        Section updatedUpStation = new Section(강남역, 양재역, 1L).updateUpStationBy(인천역);
        assertThat(updatedUpStation).isEqualTo(new Section(인천역, 양재역, 1L));
        assertThat(updatedUpStation.updateDownStationBy(강남역)).isEqualTo(new Section(인천역, 강남역, 1L));
    }

}
