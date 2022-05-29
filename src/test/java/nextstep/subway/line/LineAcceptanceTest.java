package nextstep.subway.line;

import static java.util.stream.Collectors.toList;
import static nextstep.subway.SubwayAppBehaviors.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = 지하철노선을_생성한다(
                "2호선", "color", "강남역", "충정로역", 100
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<Line> lines = 지하철노선목록을_조회한다();
        List<String> lineNames = lines.stream().map((line) -> line.getName()).collect(toList());
        assertThat(lineNames.contains("2호선")).isTrue();
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선을_생성한다("1호선", "color1", "서울역", "인천역", 100);
        지하철노선을_생성한다("2호선", "color2", "강남역", "충정로역", 100);

        // when
        List<Line> lines = 지하철노선목록을_조회한다();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long lineId = 지하철노선을_생성하고_ID를_반환한다("1호선", "color1", "서울역", "인천역", 100);

        // when
        Line line = 지하철노선을_조회한다(lineId);
        List<Station> stations = line.getStations();
        List<String> stationNames = stations.stream().map((station) -> station.getName()).collect(toList());

        // then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getColor()).isEqualTo("color1");
        assertThat(stationNames).containsExactly("서울역", "인천역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long lineId = 지하철노선을_생성하고_ID를_반환한다("1호선", "color1", "서울역", "인천역", 100);
        String newName = "1호선수정";
        String newColor = "color2";

        // when
        지하철노선을_수정한다(newName,newColor,lineId);
        Line line = 지하철노선을_조회한다(lineId);

        // then
        assertThat(line).isNotNull();
        assertThat(line.getName()).isEqualTo(newName);
        assertThat(line.getColor()).isEqualTo(newColor);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long lineId = 지하철노선을_생성하고_ID를_반환한다("1호선", "color1", "서울역", "인천역", 100);

        // when
        지하철노선을_삭제한다(lineId);
        Line line = 지하철노선을_조회한다(lineId);

        // then
        assertThat(line).isNull();
    }
}
