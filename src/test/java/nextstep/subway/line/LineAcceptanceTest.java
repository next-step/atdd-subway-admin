package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationRequestTestUtil;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineRequestTestUtil.종점역을_생성한_후_지하철_노선_생성_요청("신분당선", "bg-red-600", "강남역", "역삼역", "10");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.jsonPath().getString("id")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> responseUpStation = StationRequestTestUtil.지하철역_생성("강남역");
        ExtractableResponse<Response> responseDownStation = StationRequestTestUtil.지하철역_생성("역삼역");
        String upStationId = responseUpStation.jsonPath().getString("id");
        String downStationId = responseDownStation.jsonPath().getString("id");

        LineRequestTestUtil.종점_정보를_포함한_지하철_노선_생성_요청("신분당선", "bg-red-600", upStationId, downStationId, "10");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineRequestTestUtil.종점_정보를_포함한_지하철_노선_생성_요청("신분당선", "bg-red-600",
            upStationId, downStationId, "10");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = LineRequestTestUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = LineRequestTestUtil.지하철_노선_생성_요청("2호선", "bg-green-600");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineRequestTestUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_조회_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        long expectedId = createResponse.jsonPath().getLong("id");
        assertThat(response.jsonPath().getLong("id")).isEqualTo(expectedId);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineRequestTestUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_수정_요청
        String uri = createResponse.header("Location");
        Map<String, String> paramsUpdate = new HashMap<>();
        paramsUpdate.put("name", "구분당선");
        paramsUpdate.put("color", "bg-blue-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(paramsUpdate)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> responseAfterUpdate = 지하철_노선_조회_요청(uri);
        assertThat(responseAfterUpdate.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseAfterUpdate.jsonPath().getString("name")).isEqualTo("구분당선");
        assertThat(responseAfterUpdate.jsonPath().getString("color")).isEqualTo("bg-blue-600");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = LineRequestTestUtil.지하철_노선_생성_요청("신분당선", "bg-red-600");

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
