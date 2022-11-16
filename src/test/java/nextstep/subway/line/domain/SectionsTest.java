package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotConnectSectionException;
import nextstep.subway.exception.UpdateExistingSectionException;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("구간 목록 클래스 테스트")
class SectionsTest {

    private Line line;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "bg-green-600");
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
    }

    @Test
    void 역과_역_사이에_새로운_구간을_추가한다() {
        Section newSection = new Section(교대역, 역삼역, 10);
        line.addSection(newSection);

        line.updateSections(new Section(교대역, 강남역, 7), Arrays.asList(newSection));

        List<Section> sections = line.getSections();
        assertAll(
                () -> assertThat(sections).hasSize(2),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(강남역)
                            && section.getDownStation().equals(역삼역) && section.getDistance() == 3);
                }),
                () -> assertThat(sections.get(1)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(교대역)
                            && section.getDownStation().equals(강남역) && section.getDistance() == 7);
                })
        );
    }

    @Test
    void 이미_존재하는_구간은_추가할_수_없다() {
        Section section1 = new Section(교대역, 강남역, 10);
        Section section2 = new Section(강남역, 역삼역, 10);
        line.addSection(section1);
        line.addSection(section2);

        assertThatThrownBy(() -> {
            line.updateSections(new Section(교대역, 역삼역, 7), Arrays.asList(section1, section2));
        }).isInstanceOf(UpdateExistingSectionException.class)
                .hasMessage(SectionExceptionCode.CANNOT_UPDATE_SAME_SECTION.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = { 10, 20, 30 })
    void 거리가_추가하려는_구간에_지정된_거리보다_크거나_같으면_구간을_추가할_수_없다(int addDistance) {
        Section section = new Section(교대역, 역삼역, 10);
        line.addSection(section);

        assertThatThrownBy(() -> {
            line.updateSections(new Section(교대역, 강남역, addDistance), Arrays.asList(section));
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SectionExceptionCode.INVALID_DISTANCE.getMessage());
    }

    @Test
    void 상행역과_하행역_둘_다_구간_목록에_없으면_구간을_추가할_수_없다() {
        Station 잠실역 = new Station("잠실역");
        Station 천호역 = new Station("천호역");
        Section section = new Section(교대역, 강남역, 10);
        line.addSection(section);

        assertThatThrownBy(() -> {
            line.updateSections(new Section(잠실역, 천호역, 10), new ArrayList<>());
        }).isInstanceOf(CannotConnectSectionException.class)
                .hasMessage(SectionExceptionCode.CANNOT_CONNECT_SECTION.getMessage());
    }

    @Test
    void 맨_처음_구간을_추가한다() {
        Section newSection = new Section(강남역, 역삼역, 7);
        line.addSection(newSection);

        line.updateSections(new Section(교대역, 강남역, 10), Arrays.asList(newSection));

        List<Section> sections = line.getSections();
        assertAll(
                () -> assertThat(sections).hasSize(2),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(강남역)
                            && section.getDownStation().equals(역삼역) && section.getDistance() == 7);
                }),
                () -> assertThat(sections.get(1)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(교대역)
                            && section.getDownStation().equals(강남역) && section.getDistance() == 10);
                })
        );
    }

    @Test
    void 맨_마지막_구간을_추가한다() {
        Section newSection = new Section(교대역, 강남역, 10);
        line.addSection(newSection);

        line.updateSections(new Section(강남역, 역삼역, 15), Arrays.asList(newSection));

        List<Section> sections = line.getSections();
        assertAll(
                () -> assertThat(sections).hasSize(2),
                () -> assertThat(sections.get(0)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(교대역)
                            && section.getDownStation().equals(강남역) && section.getDistance() == 10);
                }),
                () -> assertThat(sections.get(1)).satisfies(section -> {
                    assertTrue(section.getUpStation().equals(강남역)
                            && section.getDownStation().equals(역삼역) && section.getDistance() == 15);
                })
        );
    }
}
