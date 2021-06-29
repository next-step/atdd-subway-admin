package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.exception.BelowZeroDistanceException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("Section 도메인 테스트")
class SectionTest {
    private Station 양평역;
    private Station 영등포구청역;
    private Station 영등포시장역;
    private Station 신길역;
    private Station 오목교역;

    private Line 오호선;
    private Section 구간_영등포구청역_신길역;
    private Section 구간_양평역_영등포구청역;
    private Section 구간_영등포구청역_영등포시장역;
    private Section 구간_오목교역_영등포구청역;

    private Distance 기본_거리 = new Distance(30);
    private Distance 기본의_절반_거리 = new Distance(15);

    @BeforeEach
    void 구간_생성() {
        양평역 = new Station(1L, "양평역");
        영등포구청역 = new Station(2L, "영등포구청역");
        영등포시장역 = new Station(3L, "영등포시장역");
        신길역 = new Station(4L, "신길역");
        오목교역 = new Station(5L, "오목교역");

        오호선 = new Line(1L, "5호선", "보라색", 영등포구청역, 신길역, 기본_거리);

        구간_영등포구청역_신길역 = new Section(1L, 오호선, 영등포구청역, 신길역, 기본_거리);
        구간_영등포구청역_영등포시장역 = new Section(2L, 오호선, 영등포구청역, 영등포시장역, 기본의_절반_거리);

        구간_오목교역_영등포구청역 = new Section(3L, 오호선, 오목교역, 영등포구청역, 기본_거리);
        구간_양평역_영등포구청역 = new Section(4L, 오호선, 양평역, 영등포구청역, 기본_거리);
    }

    @Test
    void getUpStation_성공() {
        assertThat(구간_양평역_영등포구청역.getUpStation()).isEqualTo(양평역);
    }

    @Test
    void getDownStation_성공() {
        assertThat(구간_양평역_영등포구청역.getDownStation()).isEqualTo(영등포구청역);
    }

    @Test
    void isIncludeStation_성공() {
        assertThat(구간_양평역_영등포구청역.isIncludeStation(양평역)).isTrue();
        assertThat(구간_양평역_영등포구청역.isIncludeStation(영등포구청역)).isTrue();
    }

    @Test
    void isIncludeStation_실패() {
        assertThat(구간_양평역_영등포구청역.isIncludeStation(영등포시장역)).isFalse();
        assertThat(구간_양평역_영등포구청역.isIncludeStation(신길역)).isFalse();
    }

    @Test
    void connectSectionBetween_성공() {
        // when
        구간_영등포구청역_신길역.connectSectionBetween(구간_영등포구청역_영등포시장역);

        // Then 시장 - 신길
        assertThat(구간_영등포구청역_신길역.getUpStation()).isEqualTo(영등포시장역);
        assertThat(구간_영등포구청역_신길역.getDownStation()).isEqualTo(신길역);
        assertThat(구간_영등포구청역_신길역.getDistance()).isEqualTo(기본의_절반_거리);
    }
    @Test
    void connectSectionBetween_실패_케이스() {
        // when
        assertThatExceptionOfType(BelowZeroDistanceException.class)
                .isThrownBy(()->구간_오목교역_영등포구청역.connectSectionBetween(구간_양평역_영등포구청역));
    }

    @Test
    void isSameStationWithUpStation_케이스_별() {
        assertThat(구간_영등포구청역_신길역.isSameStationWithUpStation(영등포구청역)).isTrue();
        assertThat(구간_영등포구청역_신길역.isSameStationWithUpStation(양평역)).isFalse();
    }

    @Test
    void isSameStationWithDownStation_케이스_별() {
        assertThat(구간_영등포구청역_신길역.isSameStationWithDownStation(신길역)).isTrue();
        assertThat(구간_영등포구청역_신길역.isSameStationWithDownStation(양평역)).isFalse();
    }
}