package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineAcceptanceTestFixture.지하철_노선_생성;
import static nextstep.subway.acceptance.line.LineAcceptanceTestFixture.지하철_노선_조회;
import static nextstep.subway.acceptance.line.SectionAcceptanceTestFixture.*;
import static nextstep.subway.acceptance.station.StationAcceptanceTestFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성("강남역").as(StationResponse.class);
        양재역 = 지하철역_생성("양재역").as(StationResponse.class);
        광교역 = 지하철역_생성("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 50).as(LineResponse.class);
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
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
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
        구간_생성에_실패한다(구간_생성_응답.statusCode());
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
        구간_생성에_실패한다(구간_생성_응답.statusCode());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 상행 종점으로 등록할 경우
     * Then 새로운 역이 상행 종점으로 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void createUpStation() {
        // given
        StationResponse 신사역 = 지하철역_생성("신사역").as(StationResponse.class);

        // when
        SectionRequest 기존_구간이_포함되지_않는_구간 = new SectionRequest(신사역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), 기존_구간이_포함되지_않는_구간);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 하행 종점으로 등록할 경우
     * Then 새로운 역이 하행 종점으로 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void createDownStation() {
        // given
        StationResponse 수원역 = 지하철역_생성("수원역").as(StationResponse.class);

        // when
        SectionRequest 기존_구간이_포함되지_않는_구간 = new SectionRequest(광교역.getId(), 수원역.getId(), 15);
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), 기존_구간이_포함되지_않는_구간);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 하행 종점으로 등록할 경우
     * Then 새로운 역이 하행 종점으로 변경된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 - 하행선 동일, 상행선 변경")
    @Test
    void createBetweenUpStation() {
        // given
        StationResponse 수원역 = 지하철역_생성("수원역").as(StationResponse.class);

        // when
        SectionRequest 기존_구간이_포함되지_않는_구간 = new SectionRequest(수원역.getId(), 광교역.getId(), 13);
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성(신분당선.getId(), 기존_구간이_포함되지_않는_구간);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 노선에 구간을 생성하고
     * When 노선에 등록되어 있지 않은 역을 제거하려고 시도하면
     * Then 예외가 발생한다
     */
    @DisplayName("노선에 등록되어 있지 않은 역을 제거하는 경우 예외가 발생한다.")
    @Test
    void delete_exception01() {
        // given
        구간_생성(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 15));
        StationResponse 수원역 = 지하철역_생성("수원역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제(신분당선.getId(), 수원역.getId());

        // then
        구간_삭제에_실패한다(구간_삭제_응답.statusCode());
    }

    /**
     * Given 구간이 하나인 노선에서
     * When 역을 제거하는 경우
     * Then 예외가 발생한다
     */
    @DisplayName("구간이 하나인 노선에서 상행역을 제거하는 경우 예외가 발생한다.")
    @Test
    void delete_exception02() {
        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제(신분당선.getId(), 강남역.getId());

        // then
        구간_삭제에_실패한다(구간_삭제_응답.statusCode());
    }

    /**
     * Given 구간이 하나인 노선에서
     * When 역을 제거하는 경우
     * Then 예외가 발생한다
     */
    @DisplayName("구간이 하나인 노선에서 하행역을 제거하는 경우 예외가 발생한다.")
    @Test
    void delete_exception03() {
        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제(신분당선.getId(), 광교역.getId());

        // then
        구간_삭제에_실패한다(구간_삭제_응답.statusCode());
    }
}
