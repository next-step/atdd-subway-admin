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

import java.util.HashMap;
import java.util.Map;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 방배역;
    private StationResponse 잠실역;

    @BeforeEach
    void beforeSetUp() {
        강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class);
        역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class);
        방배역 = StationAcceptanceTest.지하철역_생성("방배역").as(StationResponse.class);
        잠실역 = StationAcceptanceTest.지하철역_생성("잠실역").as(StationResponse.class);

        신분당선 = LineAcceptanceTest.지하철_노선_생성("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10)
                .as(LineResponse.class);
    }
    
    @DisplayName("역 사이에 새로운 역을 등록 - 상행 종점역이 일치하는 경우")
    @Test
    void addSection1() {
        // 강남역-역삼역 -> 강남역-방배역-역삼역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 강남역.getId(), 방배역.getId(), 5);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록 - 하행 종점역이 일치하는 경우")
    @Test
    void addSection2() {
        // 강남역-역삼역 -> 강남역-방배역-역삼역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 방배역.getId(), 역삼역.getId(), 5);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addNewUpStation() {
        // 강남역-역삼역 -> 역삼역-강남역-방배역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 방배역.getId(), 강남역.getId(), 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addNewDownStation() {
        // 강남역-역삼역 -> 강남역-역삼역-방배역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 역삼역.getId(), 방배역.getId(), 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음")
    @Test
    void addSectionException1() {
        // 강남역-역삼역(10), new : 강남역-방배역(15)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 강남역.getId(), 방배역.getId(), 15);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이랑 같으면 등록을 할 수 없음")
    @Test
    void addSectionException2() {
        // 강남역-역삼역(10), new : 강남역-방배역(10)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 강남역.getId(), 방배역.getId(), 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException3() {
        // 강남역-역삼역(10), new : 강남역-역삼역(10)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 강남역.getId(), 역삼역.getId(), 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException4() {
        // 강남역-역삼역, new : 방배역-잠실역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선.getId(), 방배역.getId(), 잠실역.getId(), 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("중간역 제거")
    @Test
    void deleteMiddleStation() {
        // given
        새로운_역_추가(신분당선.getId(), 강남역.getId(), 방배역.getId(), 5);    // 강남역-방배역-역삼역

        // when
        ExtractableResponse<Response> response = 지하철역_제거(신분당선.getId(), 방배역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("마지막 종점역 제거")
    @Test
    void deleteEndStation() {
        // given
        새로운_역_추가(신분당선.getId(), 역삼역.getId(), 잠실역.getId(), 5);    // 강남역-역삼역-잠실역

        // when
        ExtractableResponse<Response> response = 지하철역_제거(신분당선.getId(), 잠실역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 등록되어있지 않은 역을 제거하려 하면 Exception 발생")
    @Test
    void deleteSectionException1() {
        // when
        ExtractableResponse<Response> response = 지하철역_제거(신분당선.getId(), 잠실역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음")
    @Test
    void deleteSectionException2() {
        // 강남역-역삼역
        // when
        ExtractableResponse<Response> response = 지하철역_제거(신분당선.getId(), 역삼역.getId());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 새로운_역_추가(Long id, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_제거(Long id, Long stationId) {
        return RestAssured.given().log().all()
                .queryParam("stationId", stationId)
                .when()
                .delete("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }
}
