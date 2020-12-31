package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private List<LineResponse> lineResponses = new ArrayList<>();

    @BeforeEach
    void beforeSetUp() {
        StationResponse 강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        StationResponse 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);
        StationResponse 방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(StationResponse.class);
        StationResponse 잠실역 = StationAcceptanceTest.지하철역_생성("잠실역").as(StationResponse.class);

        Long upStationId = 강남역.getId();
        Long downStationId = 역삼역.getId();

        lineResponses.add(LineAcceptanceTest.지하철_노선_생성("신분당선", "bg-red-600", upStationId, downStationId, 10)
                .as(LineResponse.class));
        lineResponses.add(LineAcceptanceTest.지하철_노선_생성("2호선", "bg-green-600", upStationId, downStationId, 20)
                .as(LineResponse.class));
    }
    
    @DisplayName("역 사이에 새로운 역을 등록 - 상행 종점역이 일치하는 경우")
    @Test
    void addSection1() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "5");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록 - 하행 종점역이 일치하는 경우")
    @Test
    void addSection2() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "2");
        params.put("distance", "5");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addNewUpStation() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "1");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addNewDownStation() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "2");
        params.put("downStationId", "3");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음")
    @Test
    void addSectionException1() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "15");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이랑 같으면 등록을 할 수 없음")
    @Test
    void addSectionException2() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException3() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException4() {
        LineResponse createLineResponse = lineResponses.get(0);
        Long id = createLineResponse.getId();
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
