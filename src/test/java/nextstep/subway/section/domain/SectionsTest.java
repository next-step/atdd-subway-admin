package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.BelowZeroDistanceException;
import nextstep.subway.section.exception.UnaddableSectionException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Section 도메인 테스트")
class SectionsTest {
    private static Distance 두배_구간_거리_60 = new Distance(60);
    private static Distance 기본_거리_30 = new Distance(30);
    private static Distance 절반_구간_거리_15 = new Distance(15);
    private static Distance 최소_거리_1 = new Distance(1);

    private static Station 양평역 = new Station(1L, "양평역");
    private static Station 영등포구청역 = new Station(2L, "영등포구청역");
    private static Station 영등포시장역 = new Station(3L, "영등포시장역");
    private static Station 신길역 = new Station(4L, "신길역");
    private static Station 오목교역 = new Station(5L, "오목교역");

    private static Line 오호선 = new Line(1L, "5호선", "보라색", 영등포구청역, 신길역, 기본_거리_30);
    private static Section 구간_영등포구청역_신길역 = new Section(1L, 오호선, 영등포구청역, 신길역, 기본_거리_30);
    private static Section 구간_영등포구청역_영등포시장역 = new Section(2L, 오호선, 영등포구청역, 영등포시장역, 절반_구간_거리_15);
    private static Section 구간_양평역_신길역 = new Section(3L, 오호선, 양평역, 신길역, 두배_구간_거리_60);

    private static Section 구간_오목교역_영등포구청역 = new Section(4L, 오호선, 오목교역, 영등포구청역, 기본_거리_30);
    private static Section 구간_양평역_영등포구청역 = new Section(5L, 오호선, 양평역, 영등포구청역, 기본_거리_30);

    private Sections 구간_컬렉션;

    @BeforeEach
    void 초기화() {
        오호선 = new Line(1L, "5호선", "보라색", 영등포구청역, 신길역, 기본_거리_30);
        구간_컬렉션 = new Sections();
    }

    @MethodSource("methodSource_add_성공")
    @ParameterizedTest
    void add_성공(Section input1, Section input2) {
        // given
        List<Station> expectedResult = new ArrayList<>(asList(양평역, 영등포구청역, 신길역));

        // when
        구간_컬렉션.add(input1);

        // Then
        assertDoesNotThrow(() -> 구간_컬렉션.add(input2));
        assertThat(구간_컬렉션.toStations().get()).isEqualTo(expectedResult);
    }

    static Stream<Arguments> methodSource_add_성공() {
        return Stream.of(
                Arguments.of(구간_영등포구청역_신길역, 구간_양평역_영등포구청역),
                Arguments.of(구간_양평역_영등포구청역, 구간_영등포구청역_신길역),
                Arguments.of(구간_양평역_신길역, 구간_영등포구청역_신길역)
        );
    }

    @Test
    void add_예외_중복된_구간() {
        // when
        구간_컬렉션.add(구간_양평역_영등포구청역);
        구간_컬렉션.add(구간_영등포구청역_신길역);

        // Then
        assertThatExceptionOfType(UnaddableSectionException.class)
                .isThrownBy(() -> 구간_컬렉션.add(구간_양평역_영등포구청역));
    }

    @Test
    void add_예외_거리부족() {
        // when
        구간_컬렉션.add(구간_오목교역_영등포구청역);

        // Then
        assertThatExceptionOfType(BelowZeroDistanceException.class)
                .isThrownBy(() -> 구간_컬렉션.add(구간_양평역_영등포구청역));
    }
}