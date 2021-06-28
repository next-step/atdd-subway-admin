package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class SectionsTest {

    private static final int 거리 = 10;
    private static final Station 신도림역 = new Station(1L, "신도림역");
    private static final Station 서울역 = new Station(2L, "서울역");
    private static final Station 교대역 = new Station(3L, "교대역");
    private static final Station 강남역 = new Station(4L, "강남역");
    private static final Station 역삼역 = new Station(5L, "역삼역");
    private static final Station 선릉역 = new Station(6L, "선릉역");
    private static final Station 신사역 = new Station(7L, "신사역");
    private static final Station 신촌역 = new Station(8L, "신촌역");
    private static final Line 지하철_1호선 = new Line("1호선", "남색", 신도림역, 서울역, 거리);
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(new Section(지하철_1호선, 신도림역, 서울역, 거리));
        sections.add(new Section(지하철_1호선, 교대역, 신도림역, 2));
        sections.add(new Section(지하철_1호선, 신도림역, 강남역, 2));
        sections.add(new Section(지하철_1호선, 역삼역, 서울역, 2));
        sections.add(new Section(지하철_1호선, 서울역, 선릉역, 2));
    }

    @DisplayName("모든 역 정보를 상행에서 하행으로 반환한다")
    @Test
    void stations() {
        assertThat(sections.stations()).isEqualTo(Arrays.asList(교대역, 신도림역, 강남역, 역삼역, 서울역, 선릉역));
    }

    @DisplayName("역 사이 추가: 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSection_TooLongDistance_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            sections.add(new Section(지하철_1호선, 서울역, 신사역, 거리))
        );
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection_AlreadyAddedStations_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            sections.add(new Section(지하철_1호선, 서울역, 신도림역, 거리))
        );
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_NotFoundStation_ExceptionThrown() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            sections.add(new Section(지하철_1호선, 신사역, 신촌역, 거리))
        );
    }

}
