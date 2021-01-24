package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineRequestTestModule;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 소요산역;
    private StationResponse 인천역;
    private StationResponse 서울역;
    private LineResponse 일호선;

    @BeforeEach
    void beforeEach() {
        소요산역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("소요산역")).as(StationResponse.class);
        인천역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("인천역")).as(StationResponse.class);
        서울역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("서울역")).as(StationResponse.class);

        일호선 = LineRequestTestModule.지하철_노선_생성_요청(LineRequest.builder()
                                    .name("1호선")
                                    .color("파란색")
                                    .upStationId(소요산역.getId())
                                    .downStationId(인천역.getId())
                                    .distance(10).build()).as(LineResponse.class);
    }

    @DisplayName("역과 역사이에 새로운 역을 등록할 경우 : 기존 상행역 - 새로운 하행역 관계")
    @Test
    void addSection() {
        // 라인 및 지하철역 3개 생성 (beforeEach)
        // 상행 하행역 등록 (beforeEach)
        // 소요산역 다음에 서울역 등록 요청 (새로운 Section 생성)
        // given
        SectionRequest sectionRequest = new SectionRequest(소요산역.getId(), 서울역.getId(), 5);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", 일호선.getId())
                .then().log().all().extract();

        // then
        // 정상적으로 등록 되었는지 확인
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
