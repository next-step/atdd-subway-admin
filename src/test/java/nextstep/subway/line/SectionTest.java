package nextstep.subway.line;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class SectionTest {
    private Station 강남역 = new Station(1L, "강남역");
    private Station 양재역 = new Station(2L, "양재역");
    private Station 청량리역 = new Station(3L, "청량리역");
    private Station 인천역 = new Station(4L, "인천역");

    @DisplayName("upStation 과 downStation , distance 정보를 가진다.")
    @Test
    void createTest() {
        Section section = new Section(1L, 강남역, 양재역, 1L);
        assertThat(section).isEqualTo(new Section(1L, 강남역, 양재역, 1L));
    }

    @DisplayName("distance 값이 0보다 큰수를 가져야 한다.")
    @Test
    void invalidCreatedDistanceTest() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Section(1L, 강남역, 양재역, 0L));
    }

    @DisplayName("downStationId or upStationId 는 optional 이지만 둘중 하나 값을 존재 해야 한다.")
    @Test
    void noHasStationInformationTest() {
        assertThatThrownBy(() -> new Section(1L, null, null, 1L))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("upStation or downStation 둘중 하나도 기존값이랑 맞지 않으면 에러를 발생한다.")
    @Test
    void updatableTest() {
        Section section = new Section(1L, 강남역, 양재역, 1L);
        assertThatThrownBy(() -> section.updatable(new Section(2L, 청량리역, 인천역, 2L)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("기존 distance 보다 새로운 section 의 distance 가 크지 않으면 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 0L})
    void invalidDistanceTest(final long input) {
        Section section = new Section(1L, 강남역, 양재역, 2L);
        assertThatThrownBy(() -> section.updatable(new Section(2L, 청량리역, 인천역, input)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 일치하고 역 사이에 새로운 역을 등록할 경우")
    @Test
    void insertMiddleWhenMatchUpStation() {

    }
}
