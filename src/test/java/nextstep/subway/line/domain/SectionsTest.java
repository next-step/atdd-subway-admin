package nextstep.subway.line.domain;

import static nextstep.subway.common.exception.ExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.DuplicateException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;

class SectionsTest {
    private Sections sections;
    private Station 강남역;
    private Station 사당역;

    @BeforeEach
    public void setUp() {
        강남역 = new Station(3L, "강남역");
        사당역 = new Station(4L, "사당역");
        sections = new Sections();
        sections.add(new Section(강남역, 사당역, 10));
        sections.add(new Section(사당역, new Station(5L, "합정역"), 10));
        sections.add(new Section(new Station(1L, "신당역"), new Station(2L, "삼성역"), 10));
        sections.add(new Section(new Station(2L, "삼성역"), 강남역, 10));
        sections.add(new Section(new Station(5L, "합정역"), new Station(6L, "신촌"), 10));
    }

    @DisplayName("상행부터 하행까지 정렬되어 지하철역 목록을 반환한다")
    @Test
    void createStations() {
        // when
        List<Station> stations = sections.createStations();

        // then
        assertThat(stations).extracting("id")
            .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @DisplayName("두개의 역이 모두 노선에 추가되어 있으면 구간 추가에 실패한다.")
    @Test
    void duplicateAllStationToSection() {
        // when && then
        assertThatThrownBy(() -> sections.addSection(new Section(강남역, 사당역, 10)))
            .isInstanceOf(DuplicateException.class)
            .hasMessage(EXIST_ALL_STATION_TO_SECTION.getMessage());

        assertThatThrownBy(() -> sections.addSection(new Section(사당역, 강남역, 10)))
            .isInstanceOf(DuplicateException.class)
            .hasMessage(EXIST_ALL_STATION_TO_SECTION.getMessage());
    }

    @DisplayName("두 개의 역이 모두 노선에 존재하지 않으면 구간 추가에 실패한다.")
    @Test
    void nonExistAllStationToSection() {
        // given
        Station 선릉역 = new Station(7L, "선릉역");
        Station 역삼역 = new Station(8L, "역삼역");

        // when && then
        assertThatThrownBy(() -> sections.addSection(new Section(선릉역, 역삼역, 10)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NON_EXIST_ALL_STATION_TO_SECTION.getMessage());

        assertThatThrownBy(() -> sections.addSection(new Section(역삼역, 선릉역, 10)))
            .isInstanceOf(NotFoundException.class)
            .hasMessage(NON_EXIST_ALL_STATION_TO_SECTION.getMessage());
    }
}
