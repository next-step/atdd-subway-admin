package nextstep.subway.section;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {

    private Sections sections = new Sections();
    private Station 강남역;
    private Station 건대입구역;

    @BeforeEach
    public void setup() {
        강남역 = new Station("강남역");
        건대입구역 = new Station("건대입구역");
    }


    @Test
    void 첫구간_등록하기() {
        //given section을 생성하고
        Section section = new Section(강남역, 건대입구역, 10);

        //when sections에 추가하면
        sections.addFisrtSection(section);

        //then sections에 추가된다.
        assertThat(sections.getSections().size()).isEqualTo(1);
    }

    @Test
    void 이미_등록된_구간_등록시_에러_발생() {
        //given section을 생성 후 sections에 추가한다.
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when 같은 section을 sections에 한번 더 추가하면 then 에러가 발생한다.
        assertThrows(IllegalArgumentException.class, () -> {
           sections.addSection(section);
        });
    }

    @Test
    void 상행역과_하행역_모두_존재하지_않는_구간_등록_시_에러_발생() {
        //given sections에 첫등록을 하고
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when sections에 상행역과 하행역 모두 존재하지 않는 구간을 추가할 시
        Station 신림역 = new Station("신림역");
        Station 합정역 = new Station("합정역");
        Section newSection = new Section(신림역, 합정역, 20);

        //then 에러가 발생한다.
        assertThrows(IllegalArgumentException.class, () -> sections.addSection(newSection));
    }

    @Test
    void 기존_역_사이_길이보다_크거나_같은_구간_등록시_에러_발생() {
        //given sections에 첫등록을 하고
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when sections에 첫등록보다 길이가 큰 구간을 등록할시 then 에러가 발생한다.
        Station 신림역 = new Station("신림역");
        Section newSection = new Section(강남역, 신림역, 15);

        assertThrows(IllegalArgumentException.class, () -> sections.addSection(newSection));
    }

    @Test
    void 새로운_역_추가하기() {
        //given sections에 첫등록을 하고
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when 새로운 역을 추가하면
        Station 삼성역 = new Station("삼성역");
        Section newSection = new Section(강남역, 삼성역, 5);
        sections.addSection(newSection);

        //then sections에서 추가된 구간을 찾을 수 있다.
        assertThat(sections.getSections().contains(newSection)).isTrue();
    }

    @Test
    void 새로운_상행역_등록하기() {
        //given sections에 첫등록을 하고
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when sections에 새로운 상행역을 등록하면
        Station 신림역 = new Station("신림역");
        Section newSection = new Section(신림역, 강남역, 30);
        sections.addSection(newSection);

        //then sections 첫구간의 upStation은 새로 등록한 상행역이다.
        assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(신림역);
    }

    @Test
    void 새로운_하행역_등록하기() {
        //given sections에 첫등록을 하고
        Section section = new Section(강남역, 건대입구역, 10);
        sections.addFisrtSection(section);

        //when sections에 새로운 하행역을 등록하면
        Station 성수역 = new Station("성수역");
        Section newSection = new Section(건대입구역, 성수역, 30);
        sections.addSection(newSection);

        //then sections 마지막 구간의 downStation은 새로 등록한 하행역이다.
        assertThat(sections.getSections().get(sections.getSections().size() - 1).getDownStation()).isEqualTo(성수역);
    }
}
