package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineStationAcceptanceTest extends AcceptanceTest {
    StationResponse 강남역;
    StationResponse 잠실역;
    LineResponse _2호선;

    @BeforeEach
    void beforeEach(){
        super.setUp();

        강남역 = 지하철역_1개_생성("강남역").as(StationResponse.class);
        잠실역 = 지하철역_1개_생성("잠실역").as(StationResponse.class);
        _2호선 = 지하철_노선_1개_생성("2호선", "bg-color-060", 강남역.getId(), 잠실역.getId(), 20).as(LineResponse.class);
    }

    /**
     * given 노선과 역을 생성하고
     * when 신규 역과 상행종점 구간을 추가하면
     * then 노선 정보 조회 시, 추가된 역까지 모두 조회된다.
     */
    @Test
    @DisplayName("상행 종점을 추가한다.")
    void addSectionBeforeUpStation(){
        // given - beforeEach

        // when
        StationResponse 사당역 = 지하철역_1개_생성("사당역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 사당역.getId(), 강남역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> lineResponse = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(lineResponse.jsonPath().getList("stations.name", String.class)).containsExactly(
                "사당역", "강남역","잠실역"
        );
    }

    /**
     * given 노선과 역을 생성하고
     * when 신규 역과 하행종점 구간을 추가하면
     * then 노선 정보 조회 시, 추가된 역까지 모두 조회된다.
     */
    @Test
    @DisplayName("하행 종점을 추가한다.")
    void addSectionAfterDownStation(){
        // given - beforeEach

        // when
        StationResponse 건대입구역 = 지하철역_1개_생성("건대입구역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 잠실역.getId(), 건대입구역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> lineResponse = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(lineResponse.jsonPath().getList("stations.name", String.class)).containsExactly(
                "강남역","잠실역","건대입구역"
        );
    }

    /**
     * given 노선과 역(a,b)을 생성하고
     * when 신규 역과 구간(a,c,b)을 추가하면
     * then 노선 정보 조회 시, 추가된 역까지 모두 조회된다.
     */
    @Test
    @DisplayName("노선 구간을 추가한다.")
    void addSection(){
        // given - beforeEach

        // when
        StationResponse 역삼역 = 지하철역_1개_생성("역삼역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> lineResponse = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(lineResponse.jsonPath().getList("stations.name", String.class)).containsExactly(
                "강남역","역삼역","잠실역"
        );
    }

    /**
     * given 노선과 역(a,b [distance 10])을 생성하고
     * when 신규 역과 구간(a,c [distance 10])을 추가하면
     * then 신규 구간 거리가 기존 구간의 거리와 같으므로, 역이 등록되지 않는다.
     */
    @Test
    @DisplayName("노선 구간 추가 시, 구간거리가 같으면 추가되지 않는다.")
    void addSectionDistanceEqualException(){
        // given - beforeEach

        // when
        StationResponse 역삼역 = 지하철역_1개_생성("역삼역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 20);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 노선과 역(a,b [distance 10])을 생성하고
     * when 신규 역과 구간(a,c [distance 15])을 추가하면
     * then 신규 구간 거리가 기존 구간의 거리보다 크므로, 역이 등록되지 않는다.
     */
    @Test
    @DisplayName("노선 구간 추가 시, 기존 구간거리보다 크면 추가되지 않는다.")
    void addSectionDistanceOverException(){
        // given - beforeEach

        // when
        StationResponse 역삼역 = 지하철역_1개_생성("역삼역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 역삼역.getId(), 25);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 노선과 역(a,b)을 생성하고
     * when 구간(a,b)을 추가하면
     * then 상행역과 하행역이 모두 기존 구간에 포함되어 있으므로, 구간을 추가할 수 없다.
     */
    @Test
    @DisplayName("노선 구간이 기존 역에 모두 포함되면 예외를 발생시킨다.")
    void addSectionAllIncludeStationException(){
        // given - beforeEach

        // when
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 잠실역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 노선과 역(a,b)을 생성하고
     * when 구간(c,d)을 추가하면
     * then 구간 내에 존재하지 않는 역들이기 때문에 구간을 추가할 수 없다.
     */
    @Test
    @DisplayName("노선 구간이 기존 역에 존재하지 않으면 예외를 발생시킨다.")
    void addSectionNotIncludeStationException(){
        // given - beforeEach

        // when
        StationResponse 시청역 = 지하철역_1개_생성("시청역").as(StationResponse.class);
        StationResponse 왕십리역 = 지하철역_1개_생성("왕십리역").as(StationResponse.class);
        ExtractableResponse<Response> response = 노선_구간_1개_추가(_2호선.getId(), 시청역.getId(), 왕십리역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 노선_구간_1개_추가(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Map<String, Object> param = new HashMap<>();
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
