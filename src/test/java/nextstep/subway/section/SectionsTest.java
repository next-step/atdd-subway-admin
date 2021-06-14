package nextstep.subway.section;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Station 서울역;
    private Station 회현역;
    private Station 명동역;
    private Station 충무로역;

    private Sections sections;

    @BeforeEach
    void setUp() {
        서울역 = new Station("서울역");
        회현역 = new Station("회현역");
        명동역 = new Station("명동역");
        충무로역 = new Station("충무로역");

        sections = new Sections();
    }

    @DisplayName("구간 등록")
    @Test
    void 구간_등록() {
        //given
        Section 서울_회현 = new Section(서울역, 회현역, 20);

        //when
        sections.add(서울_회현);

        //then
        assertThat(sections.contains(서울_회현)).isTrue();
    }

    @DisplayName("지하철역 정렬되어 조회 : 신구간의 상행역이 상행종점")
    @Test
    void 지하철역_정렬_조회_신규_상행종점() {
        //given
        Section 서울_회현 = new Section(서울역, 회현역, 20);
        Section 회현_명동 = new Section(회현역, 명동역, 30);

        //when
        sections.add(서울_회현);
        sections.add(회현_명동);

        //then
        assertThat(sections.getStations()).hasSize(3)
                .containsExactly(서울역, 회현역, 명동역);
    }

    @DisplayName("지하철역 정렬되어 조회 : 신구간의 하행역이 하행종점")
    @Test
    void 지하철역_정렬_조회_신규_하행종점() {
        //given
        Section 회현_명동 = new Section(회현역, 명동역, 30);
        Section 명동_충무로 = new Section(명동역, 충무로역, 30);

        //when
        sections.add(회현_명동);
        sections.add(명동_충무로);

        //then
        assertThat(sections.getStations()).hasSize(3)
                .containsExactly(회현역, 명동역, 충무로역);
    }

    @DisplayName("지하철역 정렬되어 조회 : 기존 구간의 상행역에 추가")
    @Test
    void 지하철역_정렬_조회_기존_상행추가() {
        //given
        Section 서울_명동 = new Section(서울역, 명동역, 30);
        sections.add(서울_명동);

        //when
        Section 서울_회현 = new Section(서울역, 회현역, 20);
        sections.add(서울_회현);

        //then
        assertThat(sections.getStations()).hasSize(3)
                .containsExactly(서울역, 회현역, 명동역);
    }

    @DisplayName("지하철역 정렬되어 조회 : 기존 구간의 하행역에 추가")
    @Test
    void 지하철역_정렬_조회_기존_하행추가() {
        //given
        Section 서울_명동 = new Section(서울역, 명동역, 30);
        sections.add(서울_명동);

        //when
        Section 회현_명동 = new Section(회현역, 명동역, 20);
        sections.add(회현_명동);

        //then
        assertThat(sections.getStations()).hasSize(3)
                .containsExactly(서울역, 회현역, 명동역);
    }

    @DisplayName("예외상황 : 신구간의 상하행역 모두 등록되어 있음")
    @Test
    void 예외_상하행역_모두_등록되어_있음() {
        //given
        Section 서울_명동 = new Section(서울역, 명동역, 30);
        sections.add(서울_명동);

        //when + then
        Section 서울_명동_추가 = new Section(서울역, 명동역, 40);

        assertThatThrownBy(() -> sections.add(서울_명동_추가))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 상하행역이 이미 모두 등록되어있습니다.");
    }

    @DisplayName("예외상황 : 신구간의 상하행역 모두 등록되어 있지 않음")
    @Test
    void 예외_상하행역_모두_등록되어_있지않음() {
        //given
        Section 서울_명동 = new Section(서울역, 명동역, 30);
        sections.add(서울_명동);

        //when + then
        Section 강남_광교 = new Section(new Station("강남역"), new Station("광교역"), 40);

        assertThatThrownBy(() -> sections.add(강남_광교))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 상하행역중 하나라도 등록이 되어 있어야 합니다.");
    }
}
