package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import static java.util.stream.Collectors.*;
import nextstep.subway.domain.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.SubwayAppBehaviors.*;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:truncate.sql")
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        int upStationId = 지하철역을_생성하고_생성된_ID를_반환한다("강남역");
        int downStationId = 지하철역을_생성하고_생성된_ID를_반환한다("충정로역");
        int distance = 30;

        // when
        ExtractableResponse<Response> response = 지하철노선을_생성한다("2호선","color",upStationId,downStationId,distance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<Line> lines = 지하철노선목록을_조회한다();
        List<String> lineNames = lines.stream().map((line)-> line.getName()).collect(toList());
        assertThat(lineNames.contains("2호선")).isTrue();
    }


    private int 지하철역을_생성하고_생성된_ID를_반환한다(String 역이름){
        ExtractableResponse<Response> response = 지하철역을_생성한다(역이름);
        return response.jsonPath().getInt("id");
    }
}
