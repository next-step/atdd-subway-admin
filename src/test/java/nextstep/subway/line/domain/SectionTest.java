package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 구간 클래스 테스트")
class SectionTest {

    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        line = new Line("신분당선", "bg-red-600");
        upStation = new Station("강남역");
        downStation = new Station("양재역");
    }

    @Test
    void 동등성_테스트() {
        assertEquals(new Section(upStation, downStation, 10),
                new Section(upStation, downStation, 10));
    }

    @Test
    void 구간_생성시_upStation이_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Section(null, downStation, 10);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.REQUIRED_UP_STATION.getMessage());
    }

    @Test
    void 구간_생성시_downStation이_null이면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Section(upStation, null, 10);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.REQUIRED_DOWN_STATION.getMessage());
    }

    @Test
    void 구간_생성시_upStation과_downStation이_같으면_IllegalArgumentException_발생() {
        assertThatThrownBy(() -> {
            new Section(upStation, upStation, 10);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.CANNOT_BE_THE_SAME_EACH_STATION.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, -2, -10, -50, -100 })
    void 구간_생성시_distance가_음수면_IllegalArgumentException_발생(int negativeDistance) {
        assertThatThrownBy(() -> {
            new Section(upStation, downStation, negativeDistance);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.DO_NOT_ALLOW_NEGATIVE_NUMBER_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = { "10:20", "10:10", "20:50" }, delimiter = ':')
    void 구간_수정시_수정하려는_거리값이_현재_구간의_거리값보다_크거나_같으면_수정할_수_없음(int current, int request) {
        assertThatThrownBy(() -> {
            Section section = new Section(upStation, downStation, current);
            section.divideSection(new Section(upStation, new Station("판교역"), request));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.INVALID_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = { "10:20", "10:10", "20:50" }, delimiter = ':')
    void 구간_삭제시_거리_연장(int current, int request) {
        Section section = new Section(upStation, downStation, current);
        section.extendSection(new Section(downStation, upStation, request));

        assertEquals((current + request), section.getDistance());
    }
}
