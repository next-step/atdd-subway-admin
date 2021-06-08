package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nextstep.subway.PageController.URIMapping.LINE;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private RestAssuredTemplate restAssuredTemplate = new RestAssuredTemplate(LINE);
    private LineRequest param1;
    private LineRequest param2;

    @BeforeEach
    public void register() {
        //setTestParameter();
        // 사전에_등록된_상행_하행
        ExtractableResponse<Response> stationResponse1 = StationAcceptanceTest.requestCreateStation(
                StationRequest.builder().name("강남역").build()
        );
        ExtractableResponse<Response> stationResponse2 = StationAcceptanceTest.requestCreateStation(
                StationRequest.builder().name("역삼역").build()
        );
        ExtractableResponse<Response> stationResponse3 = StationAcceptanceTest.requestCreateStation(
                StationRequest.builder().name("가산디지털단지역").build()
        );
        ExtractableResponse<Response> stationResponse4 = StationAcceptanceTest.requestCreateStation(
                StationRequest.builder().name("신도림역").build()
        );

        param1 = LineRequest.builder()
                .name("1호선")
                .color("blue lighten-1")
                .upStationId(restAssuredTemplate.getLocationId(stationResponse1))
                .downStationId(restAssuredTemplate.getLocationId(stationResponse2))
                .distance(1)
                .build();

        param2 = LineRequest.builder()
                .name("2호선")
                .color("green lighten-1")
                .upStationId(restAssuredTemplate.getLocationId(stationResponse3))
                .downStationId(restAssuredTemplate.getLocationId(stationResponse4))
                .distance(1)
                .build();
    }

    @DisplayName("지하철 노선을 생성한다. 노선 생성시 종점역(상행, 하행) 정보를 반드시 추가해야한다.")
    @Test
    void createLineFail1() {
        // when
        // 잘못된 지하철_노선_생성_요청
        ExtractableResponse<Response> response1 = requestCreatedLine(LineRequest.builder()
                .name("1호선").color("blue lighten-1").build());

        ExtractableResponse<Response> response2 = requestCreatedLine(LineRequest.builder()
                .name("1호선").color("blue lighten-1").upStationId(1L).build());

        ExtractableResponse<Response> response3 = requestCreatedLine(LineRequest.builder()
                .name("1호선").color("blue lighten-1").downStationId(2L).build());

        // then
        // 지하철_노선_생성_실패
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response3.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 생성한다. 노선 생성시 종점역(상행, 하행) 정보는 사전에 등록되어 있어야 한다.")
    @Test
    void createLineFail2() {
        // when
        // 잘못된 지하철_노선_생성_요청
        ExtractableResponse<Response> response1 = requestCreatedLine(LineRequest.builder()
                .name("1호선").color("blue lighten-1").upStationId(100L).upStationId(200L).build());

        // then
        // 지하철_노선_생성_실패
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 생성한다. 노선 생성시 종점역(상행, 하행) 정보가 등록될시 정삭적으로 노선이 생성된다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreatedLine(param1);

        // then
        // 지하철_노선_생성
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenResponse = requestCreatedLine(param1);
        assertThat(givenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreatedLine(param1);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = requestCreatedLine(param1);
        ExtractableResponse<Response> createResponse2 = requestCreatedLine(param2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestShowLines();

        // then
        assertAll(
            // 지하철_노선_목록_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            // 지하철_노선_목록_포함됨
            () -> {
                List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                        .map(restAssuredTemplate::getLocationId)
                        .collect(Collectors.toList());
                List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                        .map(it -> it.getId())
                        .collect(Collectors.toList());

                assertThat(resultLineIds).containsAll(expectedLineIds);
            }
        );
    }

    @DisplayName("지하철 노선을 조회한다. 지하철 노선에는 구간정보가 포함되어있다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestShowLines(restAssuredTemplate.getLocationId(createResponse));

        // then
        assertAll(
            // 지하철_노선_응답됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),

            () -> {
                LineResponse lineResponse = response.as(LineResponse.class);

                List<StationResponse> resultLineIds = response.jsonPath().getList("$.stations", StationResponse.class)
                        .stream()
                        .collect(Collectors.toList());

                // 지하철_노선_포함됨
                assertThat(restAssuredTemplate.getLocationId(createResponse)).isEqualTo(lineResponse.getId());

                // 지하철_노선_구간정보_포함됨
                assertThat(lineResponse.getStations().size()).isGreaterThan(0);
                assertThat(lineResponse.getStations()).containsAll(resultLineIds);
            }
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(restAssuredTemplate.getLocationId(createResponse), param2);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정할때, 기존에 존재하는 노선 이름으로는 변경할수 없다.")
    @Test
    void updateLine2() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = requestCreatedLine(param1);
        ExtractableResponse<Response> createResponse2 = requestCreatedLine(param2);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(restAssuredTemplate.getLocationId(createResponse1), param2);

        // then
        // 지하철_노선_수정_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = requestCreatedLine(param1);

        // when
        // 지하철_노선_제거_요청
        long deletedId = restAssuredTemplate.getLocationId(createResponse);
        ExtractableResponse<Response> deletedResponse = requestDeleteLine(deletedId);

        // then
        // 지하철_노선_삭제됨
        assertAll(
            // 지하철_노선삭제
            () -> assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),

            // 지하철_노선_찾지못함
            () -> {
                ExtractableResponse<Response> response = requestShowLines(deletedId);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            }
        );
    }

    private ExtractableResponse<Response> requestShowLines() {
        return restAssuredTemplate.get();
    }

    private ExtractableResponse<Response> requestShowLines(final Long id) {
        return restAssuredTemplate.get(id);
    }

    private ExtractableResponse<Response> requestCreatedLine(final LineRequest param) {
        return restAssuredTemplate.post(param.toMap());
    }

    private ExtractableResponse<Response> requestUpdateLine(final Long id, final LineRequest param) {
        return restAssuredTemplate.put(id, param.toMap());
    }

    private ExtractableResponse<Response> requestDeleteLine(final Long id) {
        return restAssuredTemplate.delete(id);
    }
}
