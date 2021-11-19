package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String API_URL = "lines";

    private List<ExtractableResponse<Response>> given_상행_하행저장한다(Station upStation,
        Station downStation) {
        ExtractableResponse<Response> upResponse = givenData_저장한다(upStation, "/stations");
        ExtractableResponse<Response> downResponse = givenData_저장한다(downStation, "/stations");
        return Arrays.asList(upResponse, downResponse);
    }

    private ExtractableResponse<Response> given_1호선저장되어있음() {
        List<ExtractableResponse<Response>> responseList = given_상행_하행저장한다(new Station("서울역"),
            new Station("용산역"));

        // 이름, 색상, 상행역id, 하행역id, 거리
        LineRequest lineRequest =
            new LineRequest("1호선", "Blue", responseList.get(0).body().jsonPath().getLong("id"),
                responseList.get(1).body().jsonPath().getLong("id"), 10);

        return givenData_저장한다(lineRequest, API_URL);
    }

    @Override
    @Test
    @DisplayName("라인 생성 시 상행, 하행 정보를 요청 파라미터에 함께 추가")
    public void create() {
        //given
        //노선 생성 Request
        //상행역, 하해역 존재
        List<ExtractableResponse<Response>> responseList = given_상행_하행저장한다(new Station("서울역"),
            new Station("용산역"));
        ExtractableResponse<Response> 서울역 = responseList.get(0);
        ExtractableResponse<Response> 용산역 = responseList.get(1);

        // 이름, 색상, 상행역id, 하행역id, 거리
        LineRequest lineRequest =
            new LineRequest("1호선", "Blue", 서울역.body().jsonPath().getLong("id"),
                용산역.body().jsonPath().getLong("id"), 10);

        //when
        //지하철 노선 생성 (상행, 하행 역 정보 포함) 요청
        ExtractableResponse<Response> response = 저장한다(lineRequest, API_URL);

        //then
        //지하철 노선 생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getList("stations", StationResponse.class).size())
            .isEqualTo(2);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        given_1호선저장되어있음();
        // 역 등록되어 있음
        List<ExtractableResponse<Response>> responseList = given_상행_하행저장한다(new Station("강남역"),
            new Station("역삼역"));
        ExtractableResponse<Response> 강남역 = responseList.get(0);
        ExtractableResponse<Response> 역삼역 = responseList.get(1);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
            저장한다(new LineRequest("1호선", "Orange", 강남역.body().jsonPath().getLong("id"),
                역삼역.body().jsonPath().getLong("id"), 10), API_URL);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Override
    @Test
    public void getList() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> line1 = given_1호선저장되어있음();
        List<ExtractableResponse<Response>> responseList = given_상행_하행저장한다(new Station("강남역"),
            new Station("역삼역"));
        ExtractableResponse<Response> 강남역 = responseList.get(0);
        ExtractableResponse<Response> 역삼역 = responseList.get(1);
        ExtractableResponse<Response> line2 = givenData_저장한다(
            new LineRequest("2호선", "green", 강남역.body().jsonPath().getLong("id"),
                역삼역.body().jsonPath().getLong("id"), 10), API_URL);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 조회한다(API_URL);

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = getIdsByResponse(Arrays.asList(line1, line2), Long.class);
        List<Long> resultLineIds = response.jsonPath().getList("id", Long.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(resultLineIds).containsAll(expectedLineIds);
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Override
    @Test
    public void getOne() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData = given_1호선저장되어있음();

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 조회한다(givenData.header("Location"));

        // then
        // 지하철_노선_응답됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name")
                .equals(givenData.body().jsonPath().get("name"))).isTrue();
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Override
    @Test
    public void update() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData = given_1호선저장되어있음();

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response =
            수정한다(new LineRequest("3호선", "Orange"), givenData.header("Location"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Override
    @Test
    public void delete() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> givenData = given_1호선저장되어있음();

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 삭제한다(givenData.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


}
