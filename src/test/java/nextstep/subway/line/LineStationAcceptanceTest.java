package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("지하철 구간 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineStationAcceptanceTest extends LineStationAcceptanceTestFixture {

    /**
     * Given 지하철 역과 노선을 생성하고
     * When 지하철 노선에 지하철역 등록 요청하면
     * Then 지하철 노선에 지하철역 등록된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void 지하철_구간_등록() {
        //Given
        Station 강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(Station.class);
        Station 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(Station.class);
        Line _2호선 = LineAcceptanceTest.지하철_노선_생성("2호선", "green").as(Line.class);

        //When
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 강남역.getId() + "");
        params.put("downStationId", 역삼역.getId() + "");
        params.put("distance", 10 + "");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + _2호선.getId() + "/sections")
                .then().log().all()
                .extract();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //Then
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/" + _2호선.getId() + "/sections")
                .then().log().all()
                .extract();
        LineStations 조회된_구간정보_목록 = response2.as(LineStations.class);
        assertThat(조회된_구간정보_목록.getLineStations()).containsAnyOf(response.as(LineStation.class));
    }

    /**
     * Given 지하철 역을 노선에 등록하고
     * When 지하철 노선에 등록된 구간정보 목록을 조회하면
     * Then 지하철 노선에 등록된 구간정보 목록이 조회된다
     */
    @DisplayName("지하철 노선에 등록된 지하철역 목록조회")
    @Test
    void 노선에_등록된_역_목록_조회() {
        //Given
        Station 강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(Station.class);
        Station 역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(Station.class);
        Line _2호선 = LineAcceptanceTest.지하철_노선_생성("2호선", "green").as(Line.class);

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 강남역.getId() + "");
        params.put("downStationId", 역삼역.getId() + "");
        params.put("distance", 10 + "");
        LineStation 등록된_구간정보 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + _2호선.getId() + "/sections")
                .then().log().all()
                .extract().as(LineStation.class);

        //When
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/" + _2호선.getId() + "/sections")
                .then().log().all()
                .extract();

        //Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        //Then
        LineStations 조회된_구간정보_목록 = response.as(LineStations.class);
        assertThat(조회된_구간정보_목록.contains(등록된_구간정보)).isTrue();
    }

    /**
     * Given 지하철 역을 노선에 등록하고
     * When 지하철 노선에 등록된 지하철역 구간정보를 조회하면
     * Then 지하철 노선에 등록된 지하철역 구간정보가 조회된다
     */
    @DisplayName("지하철 노선에 등록된 지하철역 구간정보 조회")
    @Test
    void 노선에_등록된_역_구간정보_조회() {
        //Given
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 상행 종점으로 등록 요청하면
     * Then 지하철 노선에 상행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 상행 종점 등록")
    @Test
    void 상행_종점_등록() {
        //
    }

    /**
     * Given 지하철 노선에 상행 종점과 하행 종점을 등록하고
     * When 지하철 노선에 새로운 역을 하행 종점으로 등록 요청하면
     * Then 지하철 노선에 하행 종점이 등록된다
     */
    @DisplayName("지하철 노선에 하행 종점 등록")
    @Test
    void 하행_종점_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 두 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고
     * When 등록된 두 역 사이에 기존 역 사이 길이보다 큰 구간길이의 지하철역을 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("지하철 노선의 두 역 사이에 기존 역 사이 길이보다 큰 새로운 역을 등록")
    @Test
    void 구간_길이_초과_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 두 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고
     * When 등록된 두 역을 다시 노선에 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("이미 노선에 등록된 역을 상행역과 하행역으로 등록")
    @Test
    void 등록된_역_중복_등록() {
        //
    }

    /**
     * 예외케이스
     * Given 4개의 지하철 역과 1개의 노선을 생성, 노선에 두 역을 등록하고
     * When 노선에 등록되지 않은 두 지하철 역을 노선에 등록하면
     * Then 지하철 노선에 지하철역이 등록되지 않는다
     */
    @DisplayName("노선에 등록되지 않은 역을 상행역과 하행역으로 등록")
    @Test
    void 등록되지_않은_역_등록() {
        //
    }
}
