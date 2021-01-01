package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
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
    private String 신분당선_id;

    private String 강남역_id;
    private String 역삼역_id;
    private String 방배역_id;
    private String 잠실역_id;

    @BeforeEach
    void beforeSetUp() {
        강남역_id = StationAcceptanceTest.지하철역_생성("강남역").as(StationResponse.class).getId().toString();
        역삼역_id = StationAcceptanceTest.지하철역_생성("역삼역").as(StationResponse.class).getId().toString();
        방배역_id = StationAcceptanceTest.지하철역_생성("방배역").as(StationResponse.class).getId().toString();
        잠실역_id = StationAcceptanceTest.지하철역_생성("잠실역").as(StationResponse.class).getId().toString();

        LineAcceptanceTest.지하철_노선_생성("신분당선", "bg-red-600", 1L, 2L, 10);
    }
    
    @DisplayName("역 사이에 새로운 역을 등록 - 상행 종점역이 일치하는 경우")
    @Test
    void addSection1() {
        // 강남역-역삼역 -> 강남역-방배역-역삼역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 강남역_id, 방배역_id, 5);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록 - 하행 종점역이 일치하는 경우")
    @Test
    void addSection2() {
        // 강남역-역삼역 -> 강남역-방배역-역삼역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 방배역_id, 역삼역_id, 5);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addNewUpStation() {
        // 강남역-역삼역 -> 역삼역-강남역-방배역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 방배역_id, 강남역_id, 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addNewDownStation() {
        // 강남역-역삼역 -> 강남역-역삼역-방배역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 역삼역_id, 방배역_id, 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음")
    @Test
    void addSectionException1() {
        // 강남역-역삼역(10), new : 강남역-방배역(15)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 강남역_id, 방배역_id, 15);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이랑 같으면 등록을 할 수 없음")
    @Test
    void addSectionException2() {
        // 강남역-역삼역(10), new : 강남역-방배역(10)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 강남역_id, 방배역_id, 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException3() {
        // 강남역-역삼역(10), new : 강남역-역삼역(10)
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 강남역_id, 역삼역_id, 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException4() {
        // 강남역-역삼역, new : 방배역-잠실역
        // when
        ExtractableResponse<Response> response = 새로운_역_추가(신분당선_id, 방배역_id, 잠실역_id, 10);

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 새로운_역_추가(String id, String upStationId, String downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", String.valueOf(distance));

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + id + "/sections")
                .then().log().all().extract();
    }
}
