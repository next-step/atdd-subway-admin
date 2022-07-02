package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Section 도메인 객체에 대한 테스트")
class SectionTest {
    public static Section 도봉마들;
    public static Section 수락노원;
    public static Section 수락마들;
    public static Section 마들노원;

    @BeforeEach
    void setUp() {
        도봉마들 = new Section(LineTest.칠호선, StationTest.도봉산역, StationTest.마들역, new Distance(8L));
        수락노원 = new Section(LineTest.칠호선, StationTest.수락산역, StationTest.노원역, new Distance(10L));
        수락마들 = new Section(LineTest.칠호선, StationTest.수락산역, StationTest.마들역, new Distance(6L));
        마들노원 = new Section(LineTest.칠호선, StationTest.마들역, StationTest.노원역, new Distance(4L));
    }

    @DisplayName("새로운 구간이 기존 노선 사이에 추가되는 구간인지 확인한다.")
    @Test
    void isBetween() {
        assertThat(수락노원.isBetweenStation(수락마들)).isTrue();
    }

    @DisplayName("구간에 포함된 역 이름을 조회한다.")
    @Test
    void getStations() {
        assertAll(
            () -> assertThat(수락노원.getStations()).containsExactly(StationTest.수락산역, StationTest.노원역),
            () -> assertThat(수락노원.getStations()).containsExactly(StationTest.수락산역, StationTest.노원역)
        );
    }

    @DisplayName("(Section)상행역이 동일한지 확인한다.")
    @Test
    void matchUpStation() {
        assertThat(수락노원.matchUpStation(수락마들)).isTrue();
    }

    @DisplayName("(Section)하행역이 동일한지 확인한다.")
    @Test
    void matchDownStation() {
        assertThat(도봉마들.matchDownStation(수락마들)).isTrue();
    }

    @DisplayName("(Station)상행역이 동일한지 확인한다.")
    @Test
    void matchUpStationWithStation() {
        assertThat(수락노원.matchUpStationWithStation(StationTest.수락산역)).isTrue();
    }

    @DisplayName("(Station)하행역이 동일한지 확인한다.")
    @Test
    void matchDownStationWithStation() {
        assertThat(도봉마들.matchDownStationWithStation(StationTest.마들역)).isTrue();
    }

    @DisplayName("상행역에 신규 구간을 추가하면 역 재배치 및 구간 거리가 수정된다.")
    @Test
    void updateSectionByUpStation() {
        수락노원.updateUpStationAndDistance(수락마들);
        assertAll(
            () -> assertThat(수락노원.getUpStation()).isEqualTo(StationTest.마들역),
            () -> assertThat(수락노원.getDownStation()).isEqualTo(StationTest.노원역),
            () -> assertThat(수락노원.getDistance()).isEqualTo(new Distance(4L))
        );
    }

    @DisplayName("하행역에 신규 구간을 추가하면 역 재배치 및 구간 거리가 수정된다.")
    @Test
    void updateSectionByDownStation() {
        수락노원.updateDownStationAndDistance(마들노원);
        assertAll(
            () -> assertThat(수락노원.getUpStation()).isEqualTo(StationTest.수락산역),
            () -> assertThat(수락노원.getDownStation()).isEqualTo(StationTest.마들역),
            () -> assertThat(수락노원.getDistance()).isEqualTo(new Distance(6L))
        );
    }

    @DisplayName("두 개의 구간을 합칠 수 있다.")
    @Test
    void merge() {
        수락마들.merge(마들노원);
        assertAll(
            () -> assertThat(수락마들.getUpStation()).isEqualTo(StationTest.수락산역),
            () -> assertThat(수락마들.getDownStation()).isEqualTo(StationTest.노원역),
            () -> assertThat(수락마들.getDistance()).isEqualTo(new Distance(10L))
        );
    }
}
