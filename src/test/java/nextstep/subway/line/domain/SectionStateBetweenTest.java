package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

class SectionStateBetweenTest {
    private Line 이호선;
    private Station 잠실역;
    private Station 강남역;
    private Station 선릉역;
    private Section section;

    @BeforeEach
    void setUp() {
        이호선 = new Line("2호선", "bg-green");
        잠실역 = new Station("잠실역");
        강남역 = new Station("강남역");
        선릉역 = new Station("선릉역");
        section = new Section(이호선, 잠실역, 강남역, 10);
    }

    @DisplayName("새로운 구간이 기존 구간 앞 사이로 추가되면 기존 구간의 시작역이 추가되는 역으로 변경된다.")
    @Test
    void addSectionBetweenBefore() {
        // given
        Section newSection = new Section(잠실역, 선릉역, 5);

        // when
        SectionStateBetween between = new SectionStateBetween();
        between.add(section, newSection);

        // then
        // 잠실 - 선릉 - 강남
        assertEquals(이호선, newSection.getLine());
        assertEquals(5, section.getDistance());
        assertThat(Arrays.asList(section.getUpStation(), section.getDownStation()))
            .containsExactly(선릉역, 강남역);
    }

    @DisplayName("새로운 구간이 기존 구간 뒤 사이로 추가되면 기존 구간의 종료역이 추가되는 역으로 변경된다.")
    @Test
    void addSectionBetweenAfter() {
        // given
        Section newSection = new Section(선릉역, 강남역, 5);

        // when
        SectionStateBetween between = new SectionStateBetween();
        between.add(section, newSection);

        // then
        // 잠실 - 선릉 - 강남
        assertEquals(이호선, newSection.getLine());
        assertEquals(5, section.getDistance());
        assertThat(Arrays.asList(section.getUpStation(), section.getDownStation()))
            .containsExactly(잠실역, 선릉역);
    }

    @DisplayName("새로운 구간의 거리가 기존 구간의 거리와 같거나 더 크면 구간 추가가 실패한다.")
    @Test
    void addSectionDistanceGreaterOrEqual() {
        // given
        Section newSection = new Section(선릉역, 강남역, 10);

        // when && then
        assertThatThrownBy(() -> new SectionStateBetween().add(section, newSection))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(GREATER_THAN_OR_EQUAL_DISTANCE.getMessage());
    }

}
