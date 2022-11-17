package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 목록 도메인 테스트")
class SectionsTest {

    @DisplayName("구간이 등록된 경우 역 정보 목록 조회")
    @Test
    void getStations() {
        Sections sections = new Sections();
        Station 신논현역 = new Station("신논현역");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");

        Section 신분당_1구간 = new Section(신논현역, 강남역, new Distance(10L));
        Section 신분당_2구간 = new Section(강남역, 양재역, new Distance(10L));

        sections.add(신분당_1구간);
        sections.add(신분당_2구간);

        assertThat(sections.getStations().size()).isEqualTo(3);
    }

    @DisplayName("구간이 등록되지 않은 경우 역 정보 목록 조회")
    @Test
    void getEmptyStations() {
        Sections sections = new Sections();
        assertThat(sections.getStations().size()).isEqualTo(0);
    }
}