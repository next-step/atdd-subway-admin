package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void before() {
        // given 구간 초기화
        // 강남역 - 역삼역 -선릉역
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        sections = Sections.from(
                Arrays.asList(Section.of(강남역, 역삼역, 10),
                        Section.of(역삼역, 선릉역, 5))
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void sectionTest1() {
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Section 새로운_구간 = Section.of(역삼역, 삼성역, 20);

        assertThatThrownBy(
                () -> sections.add(새로운_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void sectionTest2() {
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section 새로운_구간 = Section.of(강남역, 역삼역, 20);

        assertThatThrownBy(
                () -> sections.add(새로운_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void sectionTest3() {
        Station 토쿄역 = new Station("토쿄역");
        Station 간사이역 = new Station("간사이역");
        Section 새로운_구간 = Section.of(토쿄역, 간사이역, 20);

        assertThatThrownBy(
                () -> sections.add(새로운_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선에 없는 역을 삭제 하는 경우")
    void sectionTest4() {
        Station 토쿄역 = new Station("토쿄역");

        assertThatThrownBy(
                () -> sections.delete(토쿄역)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 지하철역을 삭제 하는 경우")
    void sectionTest5() {
        Station 가락시장역 = new Station("가락시장역");
        Station 경찰병원역 = new Station("경찰병원역");

        Sections line3_sections = Sections.from(
                Arrays.asList(Section.of(가락시장역, 경찰병원역, 5))
        );

        assertThatThrownBy(
                () -> line3_sections.delete(가락시장역)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
