package nextstep.subway.line;

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
import nextstep.subway.util.PreDataUtil;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
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
        preDataUtil.station(1L, "역1", null);
        preDataUtil.station(2L, "역2", null);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다")
    @Test
    void createLine() {
        // when
        LineRequestDto lineRequestDto = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        ExtractableResponse<Response> createResponse = createLine(lineRequestDto);
        checkCreateResponse(lineRequestDto, createResponse);

        // then
        ExtractableResponse<Response> fetchResponse = fetchLines();
        checkFetchResponse(lineRequestDto, fetchResponse);
    }

    private void checkFetchResponse(LineRequestDto lineRequestDto, ExtractableResponse<Response> fetchResponse) {
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        assertThat(fetchResponse.jsonPath().getList(".")).hasSize(1);
        assertThat(fetchResponse.jsonPath().getLong("[0].id")).isNotNull();
        assertThat(fetchResponse.jsonPath().getString("[0].name")).isEqualTo(lineRequestDto.getName());
        assertThat(fetchResponse.jsonPath().getString("[0].color")).isEqualTo(lineRequestDto.getColor());
        assertThat(fetchResponse.jsonPath().getList("[0].stations")).hasSize(2);
        assertThat(fetchResponse.jsonPath().getList("[0].stations.id", Long.class)).containsExactly(1L, 2L);
        assertThat(fetchResponse.jsonPath().getList("[0].stations.name", String.class)).containsExactly("역1", "역2");
    }

    private void checkCreateResponse(LineRequestDto lineRequestDto, ExtractableResponse<Response> createResponse) {
        assertThat(HttpStatus.valueOf(createResponse.statusCode())).isEqualTo(CREATED);
        assertThat(createResponse.jsonPath().getLong("id")).isNotNull();
        assertThat(createResponse.jsonPath().getString("name")).isEqualTo(lineRequestDto.getName());
        assertThat(createResponse.jsonPath().getList("stations.id", Long.class))
            .containsExactly(lineRequestDto.getUpStationId(), lineRequestDto.getDownStationId());
        assertThat(createResponse.jsonPath().getList("stations.name", String.class)).hasSize(2);
    }

    private ExtractableResponse<Response> fetchLines() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> createLine(LineRequestDto lineRequestDto) {
        return RestAssured.given().log().all()
            .body(lineRequestDto)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    static class LineRequestDto {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private Long distance;

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public Long getUpStationId() {
            return upStationId;
        }

        public Long getDownStationId() {
            return downStationId;
        }

        public Long getDistance() {
            return distance;
        }

        public LineRequestDto(String name, String color, Long upStationId, Long downStationId, Long distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다")
    @Test
    void getLines() {
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다")
    @Test
    void getLine() {
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한")
    @Test
    void updateLine() {
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다")
    @Test
    void deleteLine() {
    }

}
