package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.CommonMethod;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        StationResponse 양재역 = StationAcceptanceTest.지하철역_생성_요청("양재").as(StationResponse.class);

        Map<String, String> params1 = new HashMap<>();
        params1.put("upStation", 양재역.getId().toString());
        params1.put("downStation", station1.getId().toString());
        params1.put("distance", "3");

        // when
        // 지하철_노선에_지하철역 등록_요청
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + 신분당선.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(createResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //given
        StationResponse 광교역 = StationAcceptanceTest.지하철역_생성_요청("광교").as(StationResponse.class);

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStation", station2.getId().toString());
        params2.put("downStation", 광교역.getId().toString());
        params2.put("distance", "5");

        // when
        // 지하철_노선에_지하철역 등록_요청
        ExtractableResponse<Response> createResponse2 = RestAssured
                .given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + 신분당선.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

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

        assertThat(response.getSections()).hasSize(3);
    }
}
