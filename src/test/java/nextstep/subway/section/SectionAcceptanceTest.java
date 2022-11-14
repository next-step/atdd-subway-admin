package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static nextstep.subway.section.SectionAcceptanceTestFixture.*;
import static nextstep.subway.station.StationAcceptanceTestFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import nextstep.subway.AbstractAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    /**
     * Given 지하철 노선을 생성하고
     * When 역 사이에 새로운 역을 등록하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addStationBetweenLine() {
        // given
        Long 강남역_ID = 지하철역_생성("강남역").jsonPath().getLong("id");
        Long 광교역_ID = 지하철역_생성("광교역").jsonPath().getLong("id");
        Long 양재역_ID = 지하철역_생성("양재역").jsonPath().getLong("id");

        Long 신분당선_ID = 지하철_노선_생성("신분당선", "주황색", 강남역_ID, 광교역_ID, 12).jsonPath().getLong("id");

        // when
        지하철_노선_구간_추가(신분당선_ID, 강남역_ID, 양재역_ID, 1);

        // then
        List<String> result = 지하철_노선_조회(신분당선_ID).jsonPath().getList("stations.name");
        assertThat(result).hasSize(3)
            .contains("강남역", "광교역", "양재역");
    }
}
