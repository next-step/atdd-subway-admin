package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationTestFixture;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse stationUp;
    private StationResponse stationDown;
    private StationResponse stationAdd;
    private StationResponse stationAdd2;
    private LineResponse line;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        stationUp = StationTestFixture.지하철역_등록("강남역").as(StationResponse.class);
        stationDown = StationTestFixture.지하철역_등록("광교역").as(StationResponse.class);
        stationAdd = StationTestFixture.지하철역_등록("교대역").as(StationResponse.class);
        stationAdd2 = StationTestFixture.지하철역_등록("서울역").as(StationResponse.class);
        line = LineTestFixture.지하철_노선_등록("1호선", "blue", stationUp.getId(), stationDown.getId(), 10).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationUp.getId().toString());
        params.put("downStationId", stationAdd.getId().toString());
        params.put("distance", "4");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 등록하려는 구간이 이미 등록되어 있다.")
    @Test
    void addSectionIsSame() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationUp.getId().toString());
        params.put("downStationId", stationDown.getId().toString());
        params.put("distance", "5");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 존재하지 않는 역을 구간으로 등록한다.")
    @Test
    void addSectionNotExist() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationAdd.getId().toString());
        params.put("downStationId", stationAdd2.getId().toString());
        params.put("distance", "5");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존과 같은 길이의 구간을 등록한다.")
    @Test
    void addSectionBiggerDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationUp.getId().toString());
        params.put("downStationId", stationAdd.getId().toString());
        params.put("distance", "10");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 맨 앞에 구간을 등록한다.")
    @Test
    void addSectionFist() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationAdd.getId().toString());
        params.put("downStationId", stationUp.getId().toString());
        params.put("distance", "4");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선 맨 뒤에 구간을 등록한다.")
    @Test
    void addSectionLast() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", stationDown.getId().toString());
        params.put("downStationId", stationAdd.getId().toString());
        params.put("distance", "4");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", line.getId())
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
