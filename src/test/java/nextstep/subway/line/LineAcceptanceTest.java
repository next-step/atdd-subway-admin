package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.primitives.Longs.asList;
import static nextstep.subway.common.AcceptanceTestSnippet.HTTP_응답_코드_확인;
import static nextstep.subway.line.ui.LineControllerTestSnippet.*;
import static nextstep.subway.line.ui.LineControllerTestSnippet.지하철_노선_구간_삭제_요청;
import static nextstep.subway.station.ui.StationControllerTestSnippet.지하철_역_생성_요청;
import static nextstep.subway.utils.ExtractableResponseUtil.응답에서_ID_추출;
import static nextstep.subway.utils.ExtractableResponseUtil.여러_응답에서_ID_추출;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private final int 기본_역간_거리 = 30;
    private final int 구간_중간_추가_역간_거리 = 15;

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 서울대입구역;
    private StationResponse 신도림역;
    private StationResponse 사당역;
    private StationResponse 영등포구청역;

    @BeforeEach
    void 지하철_역_생성() {
        강남역 = 지하철_역_생성_요청(new StationRequest("강남역")).as(StationResponse.class);
        역삼역 = 지하철_역_생성_요청(new StationRequest("역삼역")).as(StationResponse.class);
        서울대입구역 = 지하철_역_생성_요청(new StationRequest("서울대입구역")).as(StationResponse.class);
        신도림역 = 지하철_역_생성_요청(new StationRequest("신도림역")).as(StationResponse.class);
        사당역 = 지하철_역_생성_요청(new StationRequest("사당역")).as(StationResponse.class);
        영등포구청역 = 지하철_역_생성_요청(new StationRequest("영등포구청역")).as(StationResponse.class);
    }

    @DisplayName("지하철_중복_노선_생성_예외_중복된_이름")
    @Test
    void 지하철_중복_노선_생성_예외_중복된_이름() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));

        // when
        ExtractableResponse<Response> secondResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));

        // then
        HTTP_응답_코드_확인(secondResponse, BAD_REQUEST);
    }

    @DisplayName("지하철_노선_구간정보와_함께_생성_성공")
    @Test
    void 지하철_노선_생성_성공() {
        // When
        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        LineResponse createdLine = createLineResponse.as(LineResponse.class);
        List<Long> stationIdsInLine = createdLine.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        HTTP_응답_코드_확인(createLineResponse, CREATED);
        assertThat(createLineResponse.header("Location")).isNotBlank();
        assertThat(stationIdsInLine).containsAll(Arrays.asList(강남역.getId(), 역삼역.getId()));
    }

    @DisplayName("지하철_노선_목록_조회_성공")
    @Test
    void 지하철_노선_목록_조회_성공() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(new LineRequest("2호선", "00FF00", 신도림역.getId(), 서울대입구역.getId(), 기본_역간_거리));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        List<Long> expectedLineIds = 여러_응답에서_ID_추출(createResponse1, createResponse2);
        List<Long> resultLineIds = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        HTTP_응답_코드_확인(response, OK);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철_노선_목록_조회_성공_데이터없음")
    @Test
    void 지하철_노선_목록_조회_성공_데이터없음() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        LinesResponse expectedResult = new LinesResponse(response.jsonPath().getList("lineResponses", LineResponse.class));

        // then
        assertThat(expectedResult.size()).isZero();
        assertThat(expectedResult.isEmpty()).isTrue();
        HTTP_응답_코드_확인(response, OK);
    }

    @DisplayName("지하철_노선_검색_성공")
    @Test
    void 지하철_노선_검색_성공() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(new LineRequest("2호선", "00FF00", 신도림역.getId(), 서울대입구역.getId(), 기본_역간_거리));
        ExtractableResponse<Response> createResponse3 = 지하철_노선_생성_요청(new LineRequest("3호선", "00FF00", 강남역.getId(), 서울대입구역.getId(), 기본_역간_거리));
        List<Long> expectedResult = 여러_응답에서_ID_추출(createResponse2, createResponse3);

        // when
        ExtractableResponse<Response> response = 지하철_노선_검색_요청(new LineRequest("", "00FF00"));
        List<Long> actualResult = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        HTTP_응답_코드_확인(response, OK);
        assertThat(actualResult).containsAll(expectedResult);
    }

    @DisplayName("지하철_노선_검색_성공_데이터없음")
    @Test
    void 지하철_노선_검색_성공_데이터없음() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_검색_요청(new LineRequest("", "00FF00"));
        LinesResponse expectedResult = new LinesResponse(response.jsonPath().getList("lineResponses", LineResponse.class));

        // then
        assertThat(expectedResult.size()).isZero();
        assertThat(expectedResult.isEmpty()).isTrue();
        HTTP_응답_코드_확인(response, OK);
    }

    @DisplayName("지하철_노선_PK_조건_조회_성공")
    @Test
    void 지하철_노선_PK_조건_조회_성공() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));

        LineResponse expectedResult = createResponse.jsonPath().getObject(".", LineResponse.class);
        Long savedId = 응답에서_ID_추출(createResponse);

        // when
        ExtractableResponse<Response> response = 지하철_노선_PK_조건_조회_요청(savedId);
        LineResponse actualResult = response.jsonPath().getObject(".", LineResponse.class);
        List<Long> stationIdsInLine = actualResult.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        HTTP_응답_코드_확인(response, OK);
        assertAll(
                () -> assertThat(actualResult.getId()).isEqualTo(expectedResult.getId()),
                () -> assertThat(actualResult.getName()).isEqualTo(expectedResult.getName()),
                () -> assertThat(actualResult.getColor()).isEqualTo(expectedResult.getColor())
        );

        assertThat(stationIdsInLine).containsAll(asList(강남역.getId(), 역삼역.getId()));
    }

    @DisplayName("지하철_노선_PK_조건_조회_성공_데이터없음")
    @Test
    void 지하철_노선_PK_조건_조회_성공_데이터없음() {
        // given
        Long targetId = Long.MAX_VALUE;

        // when
        ExtractableResponse<Response> response = 지하철_노선_PK_조건_조회_요청(targetId);

        // then
        HTTP_응답_코드_확인(response, NOT_FOUND);
    }

    @DisplayName("지하철_노선_수정_성공")
    @Test
    void 지하철_노선_수정_성공() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        Long savedId = 응답에서_ID_추출(createResponse);

        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청(savedId, updateRequest);

        // then
        HTTP_응답_코드_확인(updateResponse, OK);
        assertThat(updateResponse.jsonPath().<String>get("name")).isEqualTo(updateRequest.getName());
        assertThat(updateResponse.jsonPath().<String>get("color")).isEqualTo(updateRequest.getColor());
    }

    @DisplayName("지하철_노선_수정_예외_존재하지_않는_PK")
    @Test
    void 지하철_노선_수정_예외_존재하지_않는_PK() {
        // when
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청(Long.MAX_VALUE, new LineRequest("1호선", "0000FF"));

        // then
        HTTP_응답_코드_확인(updateResponse, NOT_FOUND);
    }

    @DisplayName("지하철_노선_수정_예외_중복된_이름")
    @Test
    void 지하철_노선_수정_예외_수정_중복된_이름() {
        // given
        지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));

        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(new LineRequest("2호선", "00FF00", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        Long savedId2 = 응답에서_ID_추출(createResponse2);

        // when
        LineRequest updateRequest = new LineRequest("1호선", "0000FF");
        ExtractableResponse<Response> updateResponse = 지하철_노선_수정_요청(savedId2, updateRequest);

        // then
        HTTP_응답_코드_확인(updateResponse, BAD_REQUEST);
    }

    @DisplayName("지하철_노선_제거_성공")
    @Test
    void 지하철_노선_제거_성공() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        Long savedId = 응답에서_ID_추출(createResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제_요청(savedId);

        // then
        HTTP_응답_코드_확인(deleteResponse, NO_CONTENT);
    }

    @DisplayName("지하철_노선_제거_예외_존재하지_않는_PK")
    @Test
    void 지하철_노선_제거_예외_존재하지_않는_PK() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선_삭제_요청(Long.MAX_VALUE);

        // then
        HTTP_응답_코드_확인(deleteResponse, NOT_FOUND);
    }

    @DisplayName("지하철_노선_구간_추가")
    @Test
    void 지하철_노선_구간_추가() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        Long savedLineId = 응답에서_ID_추출(createResponse);

        // when
        ExtractableResponse<Response> addingSectionResponse = 지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(서울대입구역.getId(), 역삼역.getId(), 구간_중간_추가_역간_거리));

        // then
        HTTP_응답_코드_확인(addingSectionResponse, CREATED);
    }

    @DisplayName("지하철_노선_여러_구간_추가시_역_정렬_확인")
    @Test
    void 지하철_노선_여러_구간_추가시_역_정렬_확인() {
        // given
        List<Long> expectedResult = Stream.of(강남역, 역삼역, 영등포구청역, 신도림역, 사당역, 서울대입구역)
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 역삼역.getId(), 신도림역.getId(), 기본_역간_거리));
        Long savedLineId = 응답에서_ID_추출(createResponse);

        // when
        지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(역삼역.getId(), 영등포구청역.getId(), 구간_중간_추가_역간_거리));
        지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(신도림역.getId(), 서울대입구역.getId(), 기본_역간_거리));
        ExtractableResponse<Response> addingSectionResponse = 지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(사당역.getId(), 서울대입구역.getId(), 구간_중간_추가_역간_거리));
        List<Long> actualResult = 지하철_노선에_속한_여러_역의_ID추출(addingSectionResponse);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
        HTTP_응답_코드_확인(addingSectionResponse, CREATED);
    }

    @DisplayName("지하철_노선의_역_삭제")
    @Test
    void 지하철_노선의_역_삭제() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(new LineRequest("1호선", "FF0000", 강남역.getId(), 역삼역.getId(), 기본_역간_거리));
        Long savedLineId = 응답에서_ID_추출(createResponse);
        List<Long> expectedResult = Stream.of(강남역, 역삼역)
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // when
        지하철_노선_구간_추가_요청(savedLineId, new SectionRequest(서울대입구역.getId(), 역삼역.getId(), 구간_중간_추가_역간_거리));
        ExtractableResponse<Response> deleteStationInLineResponse = 지하철_노선_구간_삭제_요청(savedLineId, 서울대입구역.getId());
        List<Long> actualResult = 지하철_노선에_속한_여러_역의_ID추출(deleteStationInLineResponse);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
        HTTP_응답_코드_확인(deleteStationInLineResponse, OK);
    }
}
