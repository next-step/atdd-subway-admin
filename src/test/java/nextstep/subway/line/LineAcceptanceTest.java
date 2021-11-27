package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String PATH = "/lines";
    private static final int DEFAULT_DISTANCE = 10;

    private LineRequest request1;
    private LineRequest request2;

    private Long 강남역_Id;
    private Long 판교역_Id;
    private Long 정자역_Id;
    private Long 광교역_Id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_Id = StationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("강남역"));
        판교역_Id = StationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("판교역"));
        정자역_Id = StationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("정자역"));
        광교역_Id = StationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest("광교역"));

        request1 = new LineRequest("신분당선", "bg-red-600", 강남역_Id, 정자역_Id,
            DEFAULT_DISTANCE);
        request2 = new LineRequest("2호선", "bg-green-600", 강남역_Id, 정자역_Id,
            DEFAULT_DISTANCE);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(request1, PATH);

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성(request1, PATH);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);
        지하철_노선_등록되어_있음(request2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(PATH);

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_목록_응답됨(response);

        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨(response, Arrays.asList("신분당선", "2호선"));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회(location);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 조회 할 때 없는 노선을 조회하는 경우 조회가 실패한다.")
    @Test
    void getLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_조회_요청
        String notExistLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_조회(notExistLinePath);

        // then
        // 지하철_노선_응답됨
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(location);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response);
    }

    @DisplayName("없는 지하철 노선을 수정 할 때 조회가 실패하고 수정되지 않는다.")
    @Test
    void updateLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_수정_요청
        String notExistsLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(notExistsLinePath);

        // then
        // 지하철_노선_수정되지 않음
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        String location = 지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거(location);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    @DisplayName("없는 지하철 노선을 삭제하려고 하면 조회가 실패하고 삭제되지 않는다.")
    @Test
    void deleteLineNotExists() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(request1);

        // when
        // 지하철_노선_제거_요청
        String notExistsLinePath = "/lines/2";
        ExtractableResponse<Response> response = 지하철_노선_제거(notExistsLinePath);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("노선에 구간을 등록 한다.")
    @Test
    void addSection() {
        // given
        String lineLocation = 지하철_노선_등록되어_있음(request1);

        // when
        String sectionRegisterPath = lineLocation + "/sections";
        SectionRequest request = new SectionRequest(판교역_Id, 정자역_Id, DEFAULT_DISTANCE);
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(request, sectionRegisterPath);

        // then
        지하철_구간_등록됨(response);
    }

    private void 지하철_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getLong("upStationId")).isEqualTo(판교역_Id);
        assertThat(response.body().jsonPath().getLong("downStationId")).isEqualTo(정자역_Id);
        assertThat(response.body().jsonPath().getLong("distance")).isEqualTo(DEFAULT_DISTANCE);
    }

    private ExtractableResponse<Response> 지하철_구간_등록_요청(SectionRequest request, String path) {
        return RestTestApi.post(request, path);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("구분당선");
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(String location) {
        LineRequest modifyRequest = new LineRequest("구분당선", "bg-red-600", 판교역_Id,
            정자역_Id, DEFAULT_DISTANCE);
        return 지하철_노선_수정(modifyRequest, location);
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response,
        List<String> expectedLineNames) {
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);

        List<String> lineNames = lines.stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(lineNames).containsAll(expectedLineNames);
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        List<LineResponse> lines = response.body().jsonPath().getList(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lines).isNotNull();
        assertThat(lines).hasSize(2);
    }

    private void 지하철_노선_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(response.body().jsonPath().getList("stations")).isNotNull();
        assertThat(response.body().jsonPath().getList("stations")).hasSize(2);
        assertThat(response.body().jsonPath().getList("stations.id")).isEqualTo(Lists.list(1, 3));
    }

    private String 지하철_노선_등록되어_있음(LineRequest request) {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성(request, PATH);
        return createResponse.header("Location");
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    private ExtractableResponse<Response> 지하철_노선_생성(LineRequest params, String path) {
        return RestTestApi.post(params, path);
    }

    private ExtractableResponse<Response> 지하철_노선_조회(String path) {
        return RestTestApi.get(path);
    }

    private ExtractableResponse<Response> 지하철_노선_수정(LineRequest params, String path) {
        return RestTestApi.put(params, path);
    }

    private ExtractableResponse<Response> 지하철_노선_제거(String path) {
        return RestTestApi.delete(path);
    }
}
