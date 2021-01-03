package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 모란역;
    private StationResponse 왕십리역;
    private LineResponse 수인분당선;

    @BeforeEach
    void createLine() {
        모란역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("모란역")));
        왕십리역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("왕십리역")));
        수인분당선 = 지하철_노선_생성_요청(new LineRequest("수인분당선", "YELLOW", 모란역.getId(), 왕십리역.getId(), 10)).as(LineResponse.class);
    }

    @DisplayName("지하철 노선 구간을 등록한다.")
    @Test
    void createSection() {
        StationResponse 선릉역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("선릉역")));
        ExtractableResponse<Response> 노선_구간_등록_완료 = 지하철_노선_구간_등록_요청(수인분당선.getId(), new SectionRequest(수인분당선.getId(), 모란역.getId(), 선릉역.getId(), 15));
        assertThat(노선_구간_등록_완료.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(Long id, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }
}
