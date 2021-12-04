package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.station.domain.Station;

class SectionsTest {
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;

    private Section 강남_판교_구간;
    private Section 강남_양재_구간;
    private Section 양재_판교_구간;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");

        강남_판교_구간 = new Section(강남역, 판교역, new Distance(10));
        강남_양재_구간 = new Section(강남역, 양재역, new Distance(8));
        양재_판교_구간 = new Section(양재역, 판교역, new Distance(2));
    }

    @DisplayName("구간에서 역 목록을 가져온다")
    @Test
    void getStations() {
        Sections sections = Sections.from(
            Arrays.asList(강남_양재_구간, 양재_판교_구간));

        List<Station> stations = sections.getStations();

        assertThat(stations)
            .isEqualTo(Arrays.asList(강남역, 양재역, 판교역));
    }

    @DisplayName("하행역을 포함하는 구간을 추가했을 때 업데이트 성공")
    @Test
    void update_containsDownStation() {
        Sections sections = Sections.from(
            Arrays.asList(강남_판교_구간));

        sections.update(양재_판교_구간);

        assertThat(sections.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 판교역));
    }

    @DisplayName("상행역을 포함하는 구간을 추가했을 때 업데이트 성공")
    @Test
    void update_containsUpStation() {
        Sections sections = Sections.from(
            Arrays.asList(강남_판교_구간));

        sections.update(강남_양재_구간);

        assertThat(sections.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 판교역));
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가 실패")
    @Test
    void update_errorWhenSectionsContainsAll() {
        Sections sections = Sections.from(
            Arrays.asList(강남_판교_구간));

        assertAll(
            () -> assertThatExceptionOfType(SubwayException.class)
                .isThrownBy(() -> sections.update(강남_판교_구간))
                .withMessage("이미 모두 구간에 포함되어 있습니다."),
            () -> assertThatExceptionOfType(SubwayException.class)
                .isThrownBy(() -> sections.update(new Section(판교역, 강남역, new Distance(10))))
                .withMessage("이미 모두 구간에 포함되어 있습니다.")
        );
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void deleteStation() {
        Sections sections = Sections.from(
            Arrays.asList(강남_양재_구간, 양재_판교_구간));

        sections.deleteStation(판교역);

        assertThat(sections.getStations()).isEqualTo(Arrays.asList(강남역, 양재역));
    }

    @DisplayName("존재하지 않는 지하철 역 삭제 에러")
    @Test
    void deleteStation_errorWhenStationNotExists() {
        Sections sections = Sections.from(Arrays.asList(강남_양재_구간));

        assertThatExceptionOfType(SubwayException.class)
            .isThrownBy(() -> sections.deleteStation(판교역))
            .withMessage("존재하지 않는 지하철 역 입니다.");
    }
}