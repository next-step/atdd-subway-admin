package nextstep.subway.line;

import static nextstep.subway.utils.CommonSettings.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> upStation;
    private ExtractableResponse<Response> downStation;
    private ExtractableResponse<Response> createdLineResponse;
    private ExtractableResponse<Response> createdLineResponse2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        //given
        //상행선 종점역이 등록되어 있다.
        upStation = 지하철역_생성_요청(new StationRequest("강남역"));
        //하행선 종점역이 등록되어 있다.
        downStation = 지하철역_생성_요청(new StationRequest("역삼역"));

        // when
        // 상행선,하행선을 포함한 노선 생성 요청
        createdLineResponse = 지하철_노선_생성_요청(
            new LineRequest("신분당선", "bg-red-600")
                .upStationId(지하철역에서_ID_추출(upStation))
                .downStationId(지하철역에서_ID_추출(downStation))
                .distance(10));

        // 상행선,하행선을 포함한 노선 생성 요청2
        createdLineResponse2 = 지하철_노선_생성_요청(
            new LineRequest("2호선", "bg-green-600")
                .upStationId(지하철역에서_ID_추출(upStation))
                .downStationId(지하철역에서_ID_추출(downStation))
                .distance(10));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // then
        // 지하철_노선_생성됨
        assertThat(createdLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createdLineResponse.header("Location")).isNotBlank();

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> createdSameLineResponse = 지하철_노선_생성_요청(
            new LineRequest("신분당선", "bg-red-600")
                .upStationId(지하철역에서_ID_추출(upStation))
                .downStationId(지하철역에서_ID_추출(downStation))
                .distance(10));

        // then
        // 지하철_노선_생성_실패됨
        assertThat(createdSameLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> existsLineList = 지하철_노선_목록_반환_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(existsLineList.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = 생성된_노선들에서_ID리스트_추출(Arrays.asList(createdLineResponse, createdLineResponse2));
        List<Long> resultLineIds = 반환된_노선들에서_ID리스트_추출(existsLineList);
        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> findedLineResponse = 지하철_조회_요청(생성된_노선에서_Path추출(createdLineResponse));

        // then
        // 지하철_노선_응답됨
        assertThat(findedLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(생성된_노선에서_ID_추출(createdLineResponse)).isEqualTo(조회된_노선에서_ID_추출(findedLineResponse));
        assertThat(생성된_노선에서_Sations_추출(createdLineResponse).size()).isEqualTo(2);
    }

    @DisplayName("지하철 없는 노선을 조회한다.")
    @Test
    void getNoLine() {
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> findedLineResponse = 지하철_조회_요청("/lines/10");

        // then
        // NOT_FOUND 생성
        assertThat(findedLineResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(생성된_노선에서_Path추출(createdLineResponse));

        // then
        // 지하철_노선_수정_요청됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 노선을 수정한다.")
    @Test
    void updateNoLine() {
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("/lines/10");

        // then
        // NOT_FOUND 생성
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> removedLineResponse = 지하철_노선_제거(생성된_노선에서_Path추출(createdLineResponse));

        // then
        // 지하철_노선_삭제됨
        assertThat(removedLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 지하철 노선을 제거한다.")
    @Test
    void deleteNoLine() {
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> removedLineResponse = 지하철_노선_제거("/lines/10");

        // then
        // 지하철_노선_삭제됨
        assertThat(removedLineResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private String 생성된_노선에서_Path추출(ExtractableResponse<Response> response) {
        return String.format("/lines/%d", 생성된_노선에서_ID_추출(response));
    }

    private Long 조회된_노선에서_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private Long 지하철역에서_ID_추출(ExtractableResponse<Response> upStation) {
        return upStation.jsonPath().getObject(".", StationResponse.class).getId();
    }

    private List<Long> 반환된_노선들에서_ID리스트_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(line -> line.getId())
            .collect(Collectors.toList());
    }

    private List<Long> 생성된_노선들에서_ID리스트_추출(List<ExtractableResponse<Response>> list) {
        return list.stream()
            .map(LineAcceptanceTest::생성된_노선에서_ID_추출)
            .collect(Collectors.toList());
    }

    private static Long 생성된_노선에서_ID_추출(ExtractableResponse<Response> reponse) {
        return Long.parseLong(reponse.header("Location").split("/")[2]);
    }

    private List<StationResponse> 생성된_노선에서_Sations_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getStations();
    }

}
