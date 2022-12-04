package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.util.PreDataUtil;

@DisplayName("지하철구간 삭제 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionDeleteAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    PreDataUtil preDataUtil;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        preDataUtil.truncate();
        preDataUtil.line(1L, "2호선");
        preDataUtil.station(1L, "서초역");
        preDataUtil.station(2L, "교대역");
        preDataUtil.station(3L, "강남역");
        preDataUtil.lineStation(1L, 1L, null, 1L, null);
        preDataUtil.lineStation(2L, 2L, 1L, 1L, 1);
        preDataUtil.lineStation(3L, 3L, 2L, 1L, 2);

        preDataUtil.line(2L, "3호선");
        preDataUtil.station(4L, "양재역");
        preDataUtil.station(5L, "남부터미널역");
        preDataUtil.lineStation(4L, 4L, null, 2L, null);
        preDataUtil.lineStation(5L, 5L, 4L, 2L, 1);
    }

    /**
     * When 상행종점이 제거될 경우
     * Then 그 다음 역이 종점이 됨
     */
    @DisplayName("상행종점 구간을 제거한다")
    @Test
    void deleteEdgeUpStation() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=1")
            .then().log().all()
            .extract();
        assertThat(HttpStatus.valueOf(sectionsResponse.statusCode())).isEqualTo(OK);

        // then
        ExtractableResponse<Response> fetchResponse = LineAcceptanceTest.fetchLine(1L);
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        assertThat(fetchResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(2L, 3L);
    }

    /**
     * When 하행종점이 제거될 경우
     * Then 그 전 역이 종점이 됨
     */
    @DisplayName("하행종점 구간을 제거한다")
    @Test
    void deleteEdgeDownStation() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=3")
            .then().log().all()
            .extract();
        assertThat(HttpStatus.valueOf(sectionsResponse.statusCode())).isEqualTo(OK);

        // then
        ExtractableResponse<Response> fetchResponse = LineAcceptanceTest.fetchLine(1L);
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        assertThat(fetchResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(1L, 2L);
    }

    /**
     * When 중간 구간이 제거될 경우
     * Then 그 전 구간과 그 다음 구간을 이어야함
     */
    @DisplayName("중간 구간을 제거한다")
    @Test
    void deleteMiddleSection() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=2")
            .then().log().all()
            .extract();
        assertThat(HttpStatus.valueOf(sectionsResponse.statusCode())).isEqualTo(OK);

        // then
        ExtractableResponse<Response> fetchResponse = LineAcceptanceTest.fetchLine(1L);
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        assertThat(fetchResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(1L, 3L);
    }

    /**
     * When 노선에 등록되어 있지 않은 역을 제거할 때
     * Then BadRequest 응답을 받는다
     */
    @DisplayName("존재하지 않는 역을 제거할 때")
    @Test
    void deleteInvalidStation() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .when().delete("/lines/1/sections?stationId=99")
            .then().log().all()
            .extract();

        // then
        assertThat(HttpStatus.valueOf(sectionsResponse.statusCode())).isEqualTo(BAD_REQUEST);
    }

    /**
     * When 구간이 하나인 노선의 역을 제거할 때
     * Then BadRequest 응답을 받는다
     */
    @DisplayName("구간이 하나인 노선의 역을 제거할 때")
    @Test
    void deleteOnlyOneSection() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .when().delete("/lines/2/sections?stationId=4")
            .then().log().all()
            .extract();

        // then
        assertThat(HttpStatus.valueOf(sectionsResponse.statusCode())).isEqualTo(BAD_REQUEST);
    }

}
