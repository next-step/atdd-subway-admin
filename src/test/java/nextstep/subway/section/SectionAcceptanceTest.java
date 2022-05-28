package nextstep.subway.section;

import static nextstep.subway.line.LineAssuredMethod.노선_생성_요청;
import static nextstep.subway.station.StationAssuredMethod.지하철역_생성_요청;

import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long 강남역_id;
    private Long 광교역_id;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        강남역_id = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        광교역_id = 지하철역_생성_요청("광교역").jsonPath().getLong("id");
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역_id, 광교역_id, 10);
        노선_생성_요청(신분당선);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when 지하철_노선에_구간_등록_요청

        // then 지하철_노선에_구간_등록됨
    }
}
