package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.CommonMethod;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse station1;
    StationResponse station2;
    Map<String, String> createParams;
    LineResponse 신분당선;

    @BeforeEach
    void setUpItem() {
        //given
        station1 = StationAcceptanceTest.지하철역_생성_요청("양재시민의 숲").as(StationResponse.class);
        station2 = StationAcceptanceTest.지하철역_생성_요청("상현").as(StationResponse.class);
        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStation", station1.getId() + "");
        createParams.put("downStation", station2.getId() + "");
        createParams.put("distance", 50 + "");
        신분당선 = CommonMethod.지하철_노선_생성_요청(createParams).as(LineResponse.class);

    }

    @Test
    @DisplayName("노선에 구간을 등록한다.")
    void addSection() {
        //given
        StationResponse 판교역 = StationAcceptanceTest.지하철역_생성_요청("판교").as(StationResponse.class);
        노선_구간_생성_요청(판교역, station2, "20");

        //given
        StationResponse 양재역 = StationAcceptanceTest.지하철역_생성_요청("양재").as(StationResponse.class);
        노선_구간_생성_요청(양재역, station1, "3");

        //given
        StationResponse 광교역 = StationAcceptanceTest.지하철역_생성_요청("광교").as(StationResponse.class);
        노선_구간_생성_요청(station2, 광교역, "5");

        String uri = "/lines/" + 신분당선.getId();
        // then
        // 지하철_노선에_지하철역 등록됨
        // GET
        LineResponse response = RestAssured
                .given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract()
                .as(LineResponse.class);

        assertThat(response.getSections()).hasSize(4);
    }

    @Test
    @DisplayName("노선에 구간을 제거한다.")
    void removeSection() {
        //given
        StationResponse 판교역 = StationAcceptanceTest.지하철역_생성_요청("판교").as(StationResponse.class);
        노선_구간_생성_요청(판교역, station2, "20");

        String uri1 = "/lines/" + 신분당선.getId() + "/sections";
        //when
        ExtractableResponse<Response> deleteResponse = RestAssured
                .given().log().all()
                .param("stationId", 판교역.getId().toString())
                .when()
                .delete(uri1)
                .then().log().all()
                .extract();

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
/*
        String uri2 = "/lines/" + 신분당선.getId();
        // then
        // 지하철_노선에_지하철역 등록됨
        // GET
        LineResponse getResponse = RestAssured
                .given().log().all()
                .when()
                .get(uri2)
                .then().log().all()
                .extract()
                .as(LineResponse.class);

        assertThat(getResponse.getSections()).hasSize(2);
*/
    }

    private void 노선_구간_생성_요청(StationResponse station1, StationResponse station2, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStation", station1.getId().toString());
        params.put("downStation", station2.getId().toString());
        params.put("distance", distance);

        // when
        // 지하철_노선에_지하철역 등록_요청
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + 신분당선.getId() + "/sections")
                .then().log().all()
                .extract();

        assertThat(createResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
