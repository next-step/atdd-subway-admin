package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.exception.NoRelationStationException;
import nextstep.subway.line.exception.SameStationException;
import nextstep.subway.line.exception.SingleSectionException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionsTest {

    Section 제1구간;
    Section 제2구간;
    Section 제3구간;
    private Station 강남역;
    private Station 역삼역;
    private Station 블루보틀역;
    private Station 스타벅스역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        블루보틀역 = new Station("블루보틀역");
        스타벅스역 = new Station("스타벅스역");
        제1구간 = new Section(강남역, 역삼역, 7);
        제2구간 = new Section(역삼역, 블루보틀역, 7);
        제3구간 = new Section(블루보틀역, 스타벅스역, 7);
    }

    @Test
    void 신규구간이_기존구간과_연결된곳이없으면_에러발생() {
        Sections sections = new Sections(제1구간);
        assertThatThrownBy(() -> sections.addSection(new Section(블루보틀역, 스타벅스역, 3)))
            .isInstanceOf(NoRelationStationException.class)
            .hasMessage("근접한 상행선과 하행선이 없습니다.");
    }

    @Test
    void 신규구간이_기존구간과_동일하면_에러발생() {
        Sections sections = new Sections(제1구간);
        assertThatThrownBy(() -> sections.addSection(new Section(강남역, 역삼역, 3)))
            .isInstanceOf(SameStationException.class)
            .hasMessage("상행선과 하행선이 동일한 경우 등록을 할 수 없습니다.");
    }

    @Test
    void 구간들은_순서대로_출력() {
        Sections sections = new Sections(제3구간, 제2구간, 제1구간);
        assertThat(sections.stations())
            .hasSize(4)
            .containsExactly(강남역, 역삼역, 블루보틀역, 스타벅스역);
    }

    @Test
    void 구간이_없는경우() {
        Sections sections = new Sections();
        assertThat(sections.stations())
            .hasSize(0);
    }

    @Test
    void 사이역_제거() {
        Sections sections = new Sections(제1구간, 제2구간);
        sections.removeStation(역삼역);
        assertThat(sections.getSections())
            .hasSize(1)
            .containsExactly(new Section(강남역, 블루보틀역, 14));
    }

    @Test
    void 상행_종점_제거() {
        Sections sections = new Sections(제1구간, 제2구간);
        sections.removeStation(강남역);
        assertThat(sections.getSections())
            .hasSize(1)
            .containsExactly(new Section(역삼역, 블루보틀역, 7));
    }

    @Test
    void 하행_종점_제거() {
        Sections sections = new Sections(제1구간, 제2구간);
        sections.removeStation(역삼역);
        assertThat(sections.getSections())
            .hasSize(1)
            .containsExactly(new Section(강남역, 블루보틀역, 7));
    }

    @Test
    void 마지막_구간_제거_오류() {
        Sections sections = new Sections(제1구간);
        assertThatThrownBy(() -> sections.removeStation(강남역))
            .isInstanceOf(SingleSectionException.class)
            .hasMessage("단일구간 노선의 마지막 역은 제거할 수 없습니다.");
    }

}