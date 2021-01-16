package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600"
                                                                            , upStationId, downStationId, 10);

        // then
        // 지하철_노선_생성됨
        지하철_노선_응답_결과(response, HttpStatus.CREATED);
        assertThat(LineAcceptanceUtil.지하철_노선_생성_아이디_조회(response)).isEqualTo(1);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600",
                                                upStationId, downStationId, 2);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600",
                                                                              upStationId, downStationId, 2);

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_응답_결과(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        List<Long> expected = new ArrayList<>();

        // given
        // 지하철_노선_등록되어_있음
        long upStationId1 = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId1 = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        long downStationId2 = StationAcceptanceUtil.지하철_역_생성_요청("당산역");

        ExtractableResponse<Response> response1 = LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600", upStationId1, downStationId1, 10);
        ExtractableResponse<Response> response2 = LineAcceptanceUtil.지하철_노선_생성_요청("2호선", "bg-green-600", upStationId1, downStationId2, 10);
        expected.add(LineAcceptanceUtil.지하철_노선_생성_아이디_조회(response1));
        expected.add(LineAcceptanceUtil.지하철_노선_생성_아이디_조회(response2));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_목록_조회();

        // then
        // 지하철_노선_목록_응답됨
        지하철_노선_응답_결과(response, HttpStatus.OK);

        // 지하철_노선_목록_포함됨
        List<LineResponse> result = LineAcceptanceUtil.지하철_노선_전체_응답(response);

        assertThat(result.stream().map(LineResponse::getId).collect(Collectors.toList())).containsAll(expected);
        assertThat(result.get(0).getName()).isEqualTo("신분당선");
        assertThat(result.get(0).getColor()).isEqualTo("bg-red-600");
        assertThat(result.get(1).getName()).isEqualTo("2호선");
        assertThat(result.get(1).getColor()).isEqualTo("bg-green-600");
        
        assertThat(result.get(0).getStations().get(0).getName()).isEqualTo("잠실역");
        assertThat(result.get(0).getStations().get(1).getName()).isEqualTo("판교역");
        assertThat(result.get(1).getStations().get(0).getName()).isEqualTo("잠실역");
        assertThat(result.get(1).getStations().get(1).getName()).isEqualTo("당산역");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_조회(1);
        LineResponse 지하철_노선_응답 = response.body().as(LineResponse.class);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답_결과(response, HttpStatus.OK);
        assertThat(지하철_노선_응답.getName()).isEqualTo("신분당선");
        assertThat(지하철_노선_응답.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        // 지하철_노선_수정_요청
        // when
        ExtractableResponse<Response> response =  LineAcceptanceUtil.지하철_노선_수정_요청(1, "2호선", "bg-green-600");
        LineResponse 지하철_노선_응답 = response.jsonPath().getObject(".", LineResponse.class);

        // then
        // 지하철_노선_수정됨
        지하철_노선_응답_결과(response, HttpStatus.OK);
        assertThat(지하철_노선_응답.getName()).isEqualTo("2호선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        long upStationId = StationAcceptanceUtil.지하철_역_생성_요청("잠실역");
        long downStationId = StationAcceptanceUtil.지하철_역_생성_요청("판교역");
        LineAcceptanceUtil.지하철_노선_생성_요청("신분당선", "bg-red-600", upStationId, downStationId, 10);

        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = LineAcceptanceUtil.지하철_노선_제거_요청(1);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_응답_결과(response, HttpStatus.NO_CONTENT);

        ExtractableResponse<Response> response2 = LineAcceptanceUtil.지하철_노선_조회(1);
        지하철_노선_응답_결과(response2, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void 지하철_노선_응답_결과(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
