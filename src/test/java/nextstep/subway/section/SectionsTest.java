package nextstep.subway.section;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Sections sections;
    private Station upStation;
    private Station downStation;

    private static final Distance FIRST_SECTION_DISTANCE = new Distance(10);


    @BeforeEach
    public void setUp() {
        List<Section> sections = new ArrayList<>();

        upStation = new Station(1L,"강남역");
        downStation = new Station(2L, "광교역");

        sections.add(new Section(null, upStation, Distance.getTerminalSectionDistance()));
        sections.add(new Section(upStation, downStation, FIRST_SECTION_DISTANCE));
        sections.add(new Section(downStation, null, Distance.getTerminalSectionDistance()));

        this.sections = new Sections(sections);
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 구간에 이미 등록되어 있는 경우 오류 발생 테스트")
    void upStationDownStationAlreadyExistExceptionTest() {
        assertThatThrownBy(() -> sections.addAndGetSections(upStation, downStation, new Distance(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 역방향으로 모두 구간에 이미 등록되어 있는 경우 오류 발생 테스트")
    void upStationDownStationReverseAlreadyExistExceptionTest() {
        assertThatThrownBy(() -> sections.addAndGetSections(downStation, upStation, new Distance(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역, 하행역 둘 중 하나도 포함되어 있지 않은 경우 오류 발생 테스트")
    void upStationDownStationNotExistExceptionTest() {
        //given
        Station newUpStation = new Station("새로운역");
        Station newDownStation = new Station("또다른새로운역");

        //when
        //then
        assertThatThrownBy(() -> sections.addAndGetSections(newUpStation, newDownStation, new Distance(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간을 삭제할 경우 새로 생성된 구간의 길이를 확인한다.")
    void sectionDistanceTest() {
        //given
        Station newUpStation = new Station(3L, "새로운역");
        this.sections.addAndGetSections(newUpStation, downStation, new Distance(5));

        //when
        Section newSection = this.sections.removeSectionByStationAndGetNewSection(newUpStation);

        //then
        assertThat(newSection.getDistance()).isEqualTo(FIRST_SECTION_DISTANCE);
    }
}
