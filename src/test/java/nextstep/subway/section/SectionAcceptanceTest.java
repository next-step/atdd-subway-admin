package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.LineAcceptanceTest.신분당선_생성_파라미터;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_생성_요청;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        광교역 = 지하철_역_생성_요청(광교역_생성_파라미터()).as(StationResponse.class);

        신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터(강남역, 광교역)).as(LineResponse.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionInSection() {
        //지하철역 생성요청.
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(강남역.getId(), 신규역.getId(), 2);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        //지하철역 생성요청.
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(신규역.getId(), 강남역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
        //지하철역 생성요청.
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(광교역.getId(), 신규역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @Test
    @DisplayName("상행역을 기준으로 역 사이에 새로운 역을 등록할 떼, 거리가 기존 섹션보다 큰 경우")
    void 거리가_기존_섹션보다_큰_경우() {
        // given
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(강남역.getId(), 신규역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("하행역을 기준으로 역 사이에 새로운 역을 등록할 떼, 거리가 기존 섹션보다 큰 경우")
    void 거리가_기존_섹션보다_큰_경우2() {
        // given
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(신규역.getId(), 광교역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 존재하는 경우")
    @Test
    void 상행역과_하행역이_이미_존재하는_경우() {
        // given
        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(강남역.getId(), 광교역.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 두개다 없는 경우")
    @Test
    void 상행역과_하행역_두개다_없는_경우() {
        // given
        StationResponse 신규역 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역")).as(StationResponse.class);
        StationResponse 신규역2 = 지하철_역_생성_요청(신규역_생성_파라미터("신규역2")).as(StationResponse.class);

        SectionRequest 섹션_요청_파라미터 = SectionRequest.of(신규역.getId(), 신규역2.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(섹션_요청_파라미터, 신분당선.getId());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest 섹션_요청_파라미터, Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(섹션_요청_파라미터)
                .when()
                .post("/lines/"+lineId+"/sections")
                .then().log().all()
                .extract();
    }
}
