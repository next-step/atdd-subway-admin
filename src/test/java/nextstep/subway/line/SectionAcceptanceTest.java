package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.AcceptanceTestFixture.응답코드가_일치한다;
import static nextstep.subway.line.LineAcceptanceTestFixture.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceTestFixture.지하철_노선_조회;
import static nextstep.subway.line.SectionAcceptanceTestFixture.구간_생성;
import static nextstep.subway.line.SectionAcceptanceTestFixture.지하철_노선에_구간이_추가된다;
import static nextstep.subway.station.StationAcceptanceTestFixture.지하철역_생성;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        광교역 = 지하철역_생성("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 30).as(LineResponse.class);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선에 역과 역 사이에 새로운 역을 추가하면
     * Then 해당 지하철 노선에 구간이 추가된다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 상행선 동일, 하행선 변경")
    @Test
    void createSection() {
        // given
        StationResponse 판교역 = 지하철역_생성("판교역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), new SectionRequest(강남역.getId(), 판교역.getId(), 7));

        // then
        응답코드가_일치한다(구간_생성_응답.statusCode(), HttpStatus.OK);
        지하철_노선에_구간이_추가된다(지하철_노선_조회(신분당선.getId()));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선에 동일한 구간을 추가하면
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("노선에 동일한 구간을 추가하는 경우 구간이 추가되지 않는다.")
    @Test
    void notAllowCreateSection_duplicate() {
        // when
        SectionRequest 기존_구간과_같은_구간 = new SectionRequest(강남역.getId(), 광교역.getId(), 7);
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), 기존_구간과_같은_구간);

        // then
        응답코드가_일치한다(구간_생성_응답.statusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Given 지하철역을 생성하고
     * When 생성한 지하철역이 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void notAllowCreateSection_isNotContains() {
        // given
        StationResponse 판교역 = 지하철역_생성("판교역").as(StationResponse.class);
        StationResponse 정자역 = 지하철역_생성("정자역").as(StationResponse.class);

        // when
        SectionRequest 기존_구간이_포함되지_않는_구간 = new SectionRequest(판교역.getId(), 정자역.getId(), 10);
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), 기존_구간이_포함되지_않는_구간);

        // then
        응답코드가_일치한다(구간_생성_응답.statusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
