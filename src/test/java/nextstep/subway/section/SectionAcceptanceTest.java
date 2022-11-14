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
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.util.PreDataUtil;

@DisplayName("지하철구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
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
        preDataUtil.station(1L, "강남역", 1L);
        preDataUtil.station(2L, "서초역", 1L);
        preDataUtil.station(3L, "교대역", null);
    }

    /**
     * When 노선에 신규 지하철 역을 등록하면
     * Then 노선 조회 시 생성한 역이 포함된 것을 확인할 수 있다
     */
    @DisplayName("노선에 구간을 등록한다")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> sectionsResponse = RestAssured.given().log().all()
            .body(new SectionRequestDto(2L, 3L, 10))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        ExtractableResponse<Response> fetchResponse = LineAcceptanceTest.fetchLine(1L);
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        assertThat(fetchResponse.jsonPath().getList("stations.id", Long.class)).contains(3L);
    }

    static class SectionRequestDto {
        private Long upStationId;
        private Long downStationId;
        private int distance;

        public SectionRequestDto() {
        }

        public SectionRequestDto(Long upStationId, Long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public Long getUpStationId() {
            return upStationId;
        }

        public Long getDownStationId() {
            return downStationId;
        }

        public int getDistance() {
            return distance;
        }
    }
}
