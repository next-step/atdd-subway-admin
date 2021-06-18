package nextstep.subway.section;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("지하철 구간 기능 테스트")
public class SectionTest {
    private static Line 일호선;
    private Station 구일역;
    private Station 구로역;
    private int distance;

    @BeforeEach
    void setUp() {
        일호선 = new Line("일호선", "blue");
        구일역 = new Station("구일역");
        구로역 = new Station("구로역");
        distance = 100;
    }

    @DisplayName("지하철 역에 구간을 생성한다.")
    @Test
    void createSection() {
        Section section = 구간을_생성함(구일역, 구로역, distance);

        구간이_생성됨(section, 구일역, 구로역, distance);
    }

    @DisplayName("지하철 역의 상행역과 하행역을 수정한다.")
    @Test
    void updateSection() {
        Section 기존구간 = 구간을_생성함(구일역, 구로역, distance);

        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");
        기존구간_상행선을_신구간의_하행선으로_수정_요청(기존구간, 신도림역);
        기존구간_하행선을_신구간의_상행선으로_수정_요청(기존구간, 영등포역);

        기존구간의_상행선_수정됨(기존구간, 신도림역);
        기존구간의_하행선_수정됨(기존구간, 영등포역);
    }

    @DisplayName("지하철 구간의 상행역과 하행역이 일치하는지 확인한다.")
    @Test
    void checkSameUpStationSection() {
        Section 기존구간 = 구간을_생성함(구일역, 구로역, distance);

        기존구간의_상행역_하행역_일치함(기존구간);
        기존구간의_상행역_하행역_불일치함(기존구간, new Station("신도림역"));
    }

    @DisplayName("기존 구간의 거리보다 큰 거리 입력시 오류 발생")
    @Test
    void updateSectionDistanceException() {
        Section 기존구간 = 구간을_생성함(구일역, 구로역, distance);

        기존구간보다_긴거리_상행선_추가시_예외발생(기존구간, new Station("신도림역"), 400);
        기존구간보다_긴거리_하행선_추가시_예외발생(기존구간, new Station("신도림역"), 400);
    }

    private void 기존구간보다_긴거리_하행선_추가시_예외발생(Section defaultSection, Station newStation, int inputDistance) {
        Section 신도림_구로역 = 구간을_생성함(newStation, 구로역, inputDistance);

        assertThatThrownBy(() -> {
            defaultSection.updateUpStation(신도림_구로역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("기존 구간보다 긴 거리값은 추가할수 없습니다.");
    }

    private void 기존구간보다_긴거리_상행선_추가시_예외발생(Section defaultSection, Station newStation, int inputDistance) {
        Section 구로_신도림역 = 구간을_생성함(구일역, newStation, inputDistance);

        assertThatThrownBy(() -> {
            defaultSection.updateUpStation(구로_신도림역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("기존 구간보다 긴 거리값은 추가할수 없습니다.");
    }

    public static Section 구간을_생성함(Station upStation, Station downStation, int distance) {
        return new Section(일호선, upStation, downStation, distance);
    }

    private void 구간이_생성됨(Section section, Station upStation, Station downStation, int distance) {
        assertThat(section.upStation().getId()).isEqualTo(upStation.getId());
        assertThat(section.downStation().getId()).isEqualTo(downStation.getId());
        assertThat(section.distance()).isEqualTo(distance);
    }

    private void 기존구간_상행선을_신구간의_하행선으로_수정_요청(Section defaultSection, Station station) {
        Section 구일_신도림역 = 구간을_생성함(구일역, station, 20);
        defaultSection.updateUpStation(구일_신도림역);
    }

    private void 기존구간_하행선을_신구간의_상행선으로_수정_요청(Section defaultSection, Station station) {
        Section newSection = 구간을_생성함(station, 구로역, 20);
        defaultSection.updateDownStation(newSection);
    }

    private void 기존구간의_상행선_수정됨(Section defaultSection, Station station) {
        assertThat(defaultSection.upStation()).isEqualTo(station);
    }

    private void 기존구간의_하행선_수정됨(Section defaultSection, Station station) {
        assertThat(defaultSection.downStation()).isEqualTo(station);
    }

    private void 기존구간의_상행역_하행역_일치함(Section defaultSection) {
        assertThat(defaultSection.isEqualsUpStation(구일역)).isTrue();
        assertThat(defaultSection.isEqualsDownStation(구로역)).isTrue();
    }

    private void 기존구간의_상행역_하행역_불일치함(Section defaultSection, Station notEqualStation) {
        assertThat(defaultSection.isEqualsUpStation(notEqualStation)).isFalse();
        assertThat(defaultSection.isEqualsDownStation(notEqualStation)).isFalse();
    }
}
