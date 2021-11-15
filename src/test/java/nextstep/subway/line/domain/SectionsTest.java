package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 목록 테스트")
class SectionsTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 사당역;
    private Station 방배역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        사당역 = new Station("사당역");
        방배역 = new Station("방배역");
    }

//    @Test
//    @DisplayName("지하철 구간 순서에 맞는 지하철역 목록을 반환한다.")
//    void toStations() {
//        // given
//        Sections sections = new Sections();
//        sections.add(new Section(강남역, 역삼역, new Distance(10)));
//        sections.add(new Section(역삼역, 사당역, new Distance(15)));
//
//        // when
//        List<Station> stations = sections.toStations();
//
//        // then
//        assertThat(stations).containsExactly(강남역, 역삼역, 사당역);
//    }

    @Test
    @DisplayName("상행역이 노선에 포함되어 있는 역 사이에 구간을 추가한다.")
    void add1() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        sections.add(new Section(강남역, 사당역, new Distance(6)));

        // then
        assertThat(sections).isEqualTo(new Sections(
                Arrays.asList(
                        new Section(강남역, 사당역, new Distance(6)),
                        new Section(사당역, 역삼역, new Distance(4))
                )
        ));
    }

    @Test
    @DisplayName("하행역이 노선에 포함되어 있는 역 사이에 구간을 추가한다.")
    void add2() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        sections.add(new Section(사당역, 역삼역, new Distance(6)));

        // then
        assertThat(sections).isEqualTo(new Sections(
                Arrays.asList(
                        new Section(강남역, 사당역, new Distance(4)),
                        new Section(사당역, 역삼역, new Distance(6))
                )
        ));
    }

    @Test
    @DisplayName("상행역과 하행역이 노선에 포함되어 있는 구간을 등록할 경우 예외가 발생한다.")
    void addThrowException1() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        assertThatExceptionOfType(SectionAddFailedException.class)
                .isThrownBy(() -> sections.add(new Section(강남역, 역삼역, new Distance(5))))
                .withMessageMatching("상행역과 하행역이 노선에 포함되어 있는 구간은 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 노선에 포함되어 있지 않은 구간을 등록할 경우 예외가 발생한다.")
    void addThrowException2() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        assertThatExceptionOfType(SectionAddFailedException.class)
                .isThrownBy(() -> sections.add(new Section(사당역, 방배역, new Distance(5))))
                .withMessageMatching("상행역과 하행역중 1개는 노선에 포함되어야 합니다.");
    }

    @ParameterizedTest(name = "{displayName} - {arguments}")
    @ValueSource(ints = {10, 11})
    @DisplayName("상행역이 노선에 포함되어 있는 역 사이에 구간을 등록할 경우 기존 역 사이 거리보다 크거나 같으면 예외가 발생한다.")
    void addThrowException3(int input) {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        assertThatExceptionOfType(SectionAddFailedException.class)
                .isThrownBy(() -> sections.add(new Section(강남역, 사당역, new Distance(input))))
                .withMessageMatching("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
    }

    @ParameterizedTest(name = "{displayName} - {arguments}")
    @ValueSource(ints = {10, 11})
    @DisplayName("하행역이 노선에 포함되어 있는 역 사이에 구간을 등록할 경우 기존 역 사이 거리보다 크거나 같으면 예외가 발생한다.")
    void addThrowException4(int input) {
        // given
        Sections sections = new Sections();
        sections.add(new Section(강남역, 역삼역, new Distance(10)));

        // when
        assertThatExceptionOfType(SectionAddFailedException.class)
                .isThrownBy(() -> sections.add(new Section(사당역, 역삼역, new Distance(input))))
                .withMessageMatching("역 사이에 구간을 등록 할 경우 기존 역 사이 거리보다 작아야 합니다.");
    }
}
