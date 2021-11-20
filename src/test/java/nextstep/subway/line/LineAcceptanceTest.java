package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineTestFixture.BASE_LINE_URL;
import static nextstep.subway.line.LineTestFixture.구분당선_요청_데이터;
import static nextstep.subway.line.LineTestFixture.신분당선_요청_데이터;
import static nextstep.subway.line.LineTestFixture.이호선_요청_데이터;
import static nextstep.subway.line.LineTestFixture.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineTestFixture.지하철_노선_목록_조회_요청;
import static nextstep.subway.line.LineTestFixture.지하철_노선_생성_요청;
import static nextstep.subway.line.LineTestFixture.지하철_노선_수정_요청;
import static nextstep.subway.line.LineTestFixture.지하철_노선_제거_요청;
import static nextstep.subway.line.LineTestFixture.특정_지하철_노선_조회_요청;
import static nextstep.subway.station.StationTestFixture.강남역_요청_데이터;
import static nextstep.subway.station.StationTestFixture.역삼역_요청_데이터;
import static nextstep.subway.station.StationTestFixture.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private void 지하철_노선_조회_데이터_확인(ExtractableResponse<Response> response, LineResponse... lineResponses) {
        List<Long> expectedLineIds = Arrays.stream(lineResponses)
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds)
                .containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        LineRequest lineRequest = 신분당선_요청_데이터(upStationResponse, downStationResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location"))
                        .isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        지하철_노선_생성_요청(신분당선_요청_데이터(upStationResponse, downStationResponse));

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청_데이터(upStationResponse, downStationResponse));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터(upStationResponse, downStationResponse));
        LineResponse 이호선 = 지하철_노선_등록되어_있음(이호선_요청_데이터(upStationResponse, downStationResponse));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> 지하철_노선_조회_데이터_확인(response, 신분당선, 이호선)
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터(upStationResponse, downStationResponse));

        // when
        ExtractableResponse<Response> response = 특정_지하철_노선_조회_요청(신분당선);

        // then
        assertAll(
                () -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.OK.value()),
                () -> 지하철_노선_조회_데이터_확인(response, 신분당선)
        );
    }

    @DisplayName("없는 지하철 노선을 조회한다.")
    @Test
    void getUnknownLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청(BASE_LINE_URL + "/1");

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터(upStationResponse, downStationResponse));

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(신분당선, 구분당선_요청_데이터(upStationResponse, downStationResponse));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse upStationResponse = 지하철역_등록되어_있음(강남역_요청_데이터());
        StationResponse downStationResponse = 지하철역_등록되어_있음(역삼역_요청_데이터());
        LineResponse 신분당선 = 지하철_노선_등록되어_있음(신분당선_요청_데이터(upStationResponse, downStationResponse));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선);

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
