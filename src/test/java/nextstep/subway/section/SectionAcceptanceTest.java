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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static java.util.Arrays.*;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 야탑역;
    private StationResponse 모란역;
    private StationResponse 수서역;
    private StationResponse 선릉역;
    private StationResponse 왕십리역;
    private StationResponse 청량리역;
    private List<List<StationResponse>> 지하철역 = new ArrayList<>();
    private LineResponse 수인분당선;

    @BeforeEach
    public void setUpSubway() {
        야탑역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("야탑역")));
        모란역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("모란역")));
        수서역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("수서역")));
        선릉역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("선릉역")));
        왕십리역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("왕십리역")));
        청량리역 = 생성된_지하철역(지하철역_생성_요청(new StationRequest("청량리역")));

        지하철역.add(asList(야탑역, 모란역));
        지하철역.add(asList(모란역, 수서역));
        지하철역.add(asList(선릉역, 왕십리역));
        지하철역.add(asList(왕십리역, 청량리역));

        수인분당선 = 지하철_노선_생성_요청(new LineRequest("수인분당선", "YELLOW", 모란역.getId(), 왕십리역.getId(), 10)).as(LineResponse.class);
    }

    @DisplayName("지하철 노선 구간을 등록한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void createSection(int index) {
        StationResponse 상행역 = 지하철역.get(index).get(0);
        StationResponse 하행역 = 지하철역.get(index).get(1);

        ExtractableResponse<Response> 노선_구간_등록_완료 = 지하철_노선_구간_등록_요청(수인분당선.getId(), new SectionRequest(수인분당선.getId(), 상행역.getId(), 하행역.getId(), 4));
        assertThat(노선_구간_등록_완료.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철 노선 구간을 삭제한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void deleteSection(int index) {
        StationResponse 상행역 = 지하철역.get(index).get(0);
        StationResponse 하행역 = 지하철역.get(index).get(1);
        StationResponse[] 삭제역 = new StationResponse[] {야탑역, 수서역, 선릉역, 청량리역};

        ExtractableResponse<Response> 노선_구간_등록_완료 = 지하철_노선_구간_등록_요청(수인분당선.getId(), new SectionRequest(수인분당선.getId(), 상행역.getId(), 하행역.getId(), 4));

        ExtractableResponse<Response> 노선_구간_삭제_완료 = 지하철_노선_구간_삭제_요청(수인분당선.getId(), 삭제역[index].getId());
        assertThat(노선_구간_삭제_완료.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선 구간이 하나일 때 삭제한다.")
    @Test
    void deleteSection2() {
        ExtractableResponse<Response> 노선_구간_삭제 = 지하철_노선_구간_삭제_요청(수인분당선.getId(), 모란역.getId());
        assertThat(노선_구간_삭제.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 구간의 역을 삭제한다.")
    @Test
    void deleteSection3() {
        ExtractableResponse<Response> 노선_구간_등록_완료 = 지하철_노선_구간_등록_요청(수인분당선.getId(), new SectionRequest(수인분당선.getId(), 모란역.getId(), 수서역.getId(), 4));

        ExtractableResponse<Response> 노선_구간_삭제 = 지하철_노선_구간_삭제_요청(수인분당선.getId(), 선릉역.getId());
        assertThat(노선_구간_삭제.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_구간_삭제_요청(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }
}
