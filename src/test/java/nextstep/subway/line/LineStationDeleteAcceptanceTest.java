package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.line.LineStationAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.utils.DatabaseCleanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineStationDeleteAcceptanceTest {

    @LocalServerPort
    int port;

    StationResponse 강남역;
    StationResponse 잠실역;
    StationResponse 건대입구역;
    LineResponse _2호선;

    @Autowired
    DatabaseCleanUtil databaseCleanUtil;

    @BeforeEach
    void beforeEach(){
        if(RestAssured.port == RestAssured.UNDEFINED_PORT){
           RestAssured.port = port;
        }
        databaseCleanUtil.cleanUp();

        강남역 = 지하철역_1개_생성("강남역").as(StationResponse.class);
        잠실역 = 지하철역_1개_생성("잠실역").as(StationResponse.class);
        건대입구역 = 지하철역_1개_생성("건대입구역").as(StationResponse.class);

        _2호선 = 지하철_노선_1개_생성("2호선", "bg-color-060", 강남역.getId(), 건대입구역.getId(), 20)
                .as(LineResponse.class);
    }

    /**
     * given 노선에 구간을 2개(a -> b, b -> c) 추가하고
     * when 가운데 역(b)을 제거하면
     * then 노선 조회 시, 역이 제거되고 남은 구간(a -> c)이 조회된다.
     */
    @Test
    @DisplayName("가운데 역을 제거한다.")
    void deleteStation(){
        // given - beforeEach
        노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> deleteResponse = 구간_1개_제거(잠실역.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> response = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(response.jsonPath().getList("stations.name")).containsExactly(
                "강남역", "건대입구역"
        );
    }

    /**
     * given 노선에 구간을 2개(a -> b, b -> c) 추가하고
     * when 상행종점 역(a)을 제거하면
     * then 노선 조회 시, 역이 제거되고 남은 구간(b -> c)이 조회된다.
     */
    @Test
    @DisplayName("상행종점을 제거한다.")
    void deleteFirstStation(){
        // given - beforeEach
        노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> deleteResponse = 구간_1개_제거(강남역.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> response = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(response.jsonPath().getList("stations.name")).containsExactly(
                "잠실역", "건대입구역"
        );
    }

    /**
     * given 노선에 구간을 2개(a -> b, b -> c) 추가하고
     * when 하행종점 역(c)을 제거하면
     * then 노선 조회 시, 역이 제거되고 남은 구간(a -> b)이 조회된다.
     */
    @Test
    @DisplayName("하행종점을 제거한다.")
    void deleteLastStation(){
        // given - beforeEach
        노선_구간_1개_추가(_2호선.getId(), 강남역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> deleteResponse = 구간_1개_제거(건대입구역.getId());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> response = 지하철_노선_정보_조회(_2호선.getId());
        assertThat(response.jsonPath().getList("stations.name")).containsExactly(
                "강남역", "잠실역"
        );
    }

    /**
     * given 노선에 구간을 1개(a -> b) 추가하고
     * when 역(b)을 제거하면
     * then 예외를 발생시킨다.
     */
    @Test
    @DisplayName("남은 구간이 하나인 상태에서 역을 제거하려하면 예외가 발생한다.")
    void deleteStationLastSection(){
        // given - beforeEach

        // when
        ExtractableResponse<Response> deleteResponse = 구간_1개_제거(건대입구역.getId());

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 노선에 구간을 1개(a -> b) 추가하고
     * when 존재하지 않는 역(c)을 제거하면
     * then 예외를 발생시킨다.
     */
    @Test
    @DisplayName("노선 구간에 존재하지 않는 역을 제거하려하면 예외가 발생한다.")
    void deleteStationOtherStation(){
        // given - beforeEach
        StationResponse 시청역 = 지하철역_1개_생성("시청역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = 구간_1개_제거(시청역.getId());

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 구간_1개_제거(Object id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("stationId", id)
                .when().delete("/lines/{id}/sections", _2호선.getId())
                .then().log().all()
                .extract();
    }
}
