package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    Sections sections = new Sections();
    Station upStation = new Station(1L, "경기 광주역");
    Station downStation = new Station(2L, "모란역");

    @BeforeEach
    void beforeEach() {
        sections.addDefaultSection(null, 10, upStation, downStation);
    }

    @Test
    void 구간_목록을_가져온다() {
        sections.addDefaultSection(null, 9, upStation, downStation);

        assertThat(sections.getSections()).hasSize(2);
    }


    @Test
    void 구간_길이_목록을_가져온다() {
        sections.addSection(4, new Station(3L, "판교역"), downStation);

        assertThat(sections.getDistances()).hasSize(2);
        assertThat(sections.getDistances()).contains(6, 4);
    }

    @Test
    void 구간에_포함된_역들의_이름을_중복되지_않도록_가져온다() {
        sections.addDefaultSection(null, 9, upStation, downStation);

        assertThat(sections.getStationNames()).hasSize(2);
        assertThat(sections.getStationNames()).contains("경기 광주역", "모란역");
    }

    @Test
    void 구간_목록에서_해당_구간을_제거한다() {
        sections.addDefaultSection(null, 9, upStation, downStation);

        List<Section> getSections = sections.getSections();
        sections.removeSection(getSections.get(0));

        assertThat(this.sections.getSections()).hasSize(1);
    }

    @Test
    void 구간이_하나인_노선은_제거할_수_없다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.deleteSectionByStation(null, upStation));
    }

    @DisplayName("기존 노선에 구간 추가시 상행 하행역이 모두 동일하거나 둘다 다를 경우 EX 발생")
    @Test
    void addSection() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.addSection(4, upStation, downStation));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.addSection(4, new Station(3L, "판교역"), new Station(4L, "중앙역")));
    }

    @Test
    void 상행역으로_새로운_구간_생성시_길이가_같으면_EX_발생() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.addSection(10, upStation, new Station(3L, "판교역")));
    }

    @Test
    void 상행역이_같으면_새로운_구간_생성() {
        sections.addSection(4, upStation, new Station(3L, "판교역"));

        assertThat(sections.getSections()).hasSize(2);
    }

    @Test
    void 하행역으로_새로운_구간_생성시_길이가_같으면_EX_발생() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sections.addSection(10, new Station(3L, "판교역"), downStation));
    }

    @Test
    void 하행역이_같으면_새로운_구간_생성() {
        sections.addSection(4, new Station(3L, "판교역"), downStation);

        assertThat(sections.getSections()).hasSize(2);
    }
}
