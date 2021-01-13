package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.CommonMethod;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionAcceptanceTest extends AcceptanceTest {
    StationResponse 강남역;
    StationResponse 광교역;
    Map<String, String> createParams;
    LineResponse 신분당선;

    @BeforeEach
    void setUpItem() {
        //given
        강남역 = StationAcceptanceTest.지하철역_생성_요청("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_생성_요청("광교역").as(StationResponse.class);
        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStation", 강남역.getId() + "");
        createParams.put("downStation", 광교역.getId() + "");
        createParams.put("distance", 15 + "");
        신분당선 = CommonMethod.지하철_노선_생성_요청(createParams).as(LineResponse.class);

    }

    @Test
    @DisplayName("노선에 구간을 등록한다.")
    void addSection() {
        // when
        // 지하철_노선에_지하철역 등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStation", 강남역.getId().toString());
        params.put("downStation", 광교역.getId().toString());
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + 신분당선.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역 등록됨
        // GET
    }
}
