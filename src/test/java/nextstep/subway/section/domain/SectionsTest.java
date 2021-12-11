package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;


public class SectionsTest {

    public static final Station 강남역 = new Station(1L, "강남역");
    public static final Station 양재역 = new Station(2L, "양재역");
    public static final Station 양재시민의숲 = new Station(3L, "양재시민의숲");
    public static final Station 광교역 = new Station(4L, "광교역");


    @DisplayName("상행선부터 하행선까지 정렬된 station 을 테스트")
    @Test
    public void getFirstStationTest() {

        // given
        Line 신분당선 = new Line("신분당선", "red");

        Sections 섹션 = new Sections();
        Section 강남역_양재역 = new Section(신분당선, 강남역, 양재역, 10);
        Section 양재역_양재시민의숲 = new Section(신분당선, 양재역, 양재시민의숲, 10);
        Section 양재시민의숲_광교역 = new Section(신분당선, 양재시민의숲, 광교역, 10);

        섹션.addSection(강남역_양재역);
        섹션.addSection(양재역_양재시민의숲);
        섹션.addSection(양재시민의숲_광교역);

        // when
        List<Station> stations = 섹션.getStations();

        // then
        Assertions.assertThat(stations.get(0)).isEqualTo(강남역);
        Assertions.assertThat(stations.get(1)).isEqualTo(양재역);
        Assertions.assertThat(stations.get(2)).isEqualTo(양재시민의숲);
        Assertions.assertThat(stations.get(3)).isEqualTo(광교역);
    }
}
