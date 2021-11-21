package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationStep;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineStep.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LineSectionAcceptanceTest extends AcceptanceTest {

    private static final StationRequest 교대역_요청 = new StationRequest("교대역");
    private static final StationRequest 강남역_요청 = new StationRequest("강남역");
    private static final StationRequest 역삼역_요청 = new StationRequest("역삼역");
    private static final StationRequest 삼성역_요청 = new StationRequest("삼성역");
    private static final StationRequest 잠실역_요청 = new StationRequest("잠실역");

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 삼성역;
    private StationResponse 잠실역;
    private LineResponse 이호선;

    @BeforeEach
    void beforeEach() {
        교대역 = StationStep.지하철역_생성되어_있음(교대역_요청);
        강남역 = StationStep.지하철역_생성되어_있음(강남역_요청);
        역삼역 = StationStep.지하철역_생성되어_있음(역삼역_요청);
        삼성역 = StationStep.지하철역_생성되어_있음(삼성역_요청);
        잠실역 = StationStep.지하철역_생성되어_있음(잠실역_요청);

        이호선 = LineStep.지하철_노선_등록되어_있음(new LineRequest("2호선", "bg-green-600", 강남역.getId(), 삼성역.getId(), 10));
    }

    @Test
    void addLineSection_역_사이에_하행역을_등록한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 강남역, 역삼역, 3);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_정렬됨(lineResponse, Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @Test
    void addLineSection_새로운_역을_하행종점으로_등록한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 삼성역, 잠실역, 3);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_정렬됨(lineResponse, Arrays.asList(강남역, 삼성역, 잠실역));
    }

    @Test
    void addLineSection_역_사이에_상행역을_등록한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 삼성역, 3);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_정렬됨(lineResponse, Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @Test
    void addLineSection_새로운_역을_상행종점으로_등록한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 강남역, 3);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_정렬됨(lineResponse, Arrays.asList(역삼역, 강남역, 삼성역));
    }

    @Test
    void addLineSection_새로운_역_사이의_길이가_기존_역_사이의_길이보다_크거나_같으면_에러를_발생한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 역삼역, 삼성역, 10);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @Test
    void addLineSection_이미_등록된_구간에_추가하면_에러를_발생한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 강남역, 삼성역, 10);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @Test
    void addLineSection_상행역_하행역에_포함안된_경우_에러를_발생한다() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(이호선, 잠실역, 교대역, 3);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @Test
    void removeLineSection_상행_종점역이_제거될_경우_다음_역이_상행_종점역이_된다() {
        // given
        지하철_노선에_지하철역_등록되어_있음(이호선, 강남역, 역삼역, 3);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(이호선, 강남역);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_제거됨(response);
        지하철_노선에_지하철역_모두_정렬됨(lineResponse, Arrays.asList(역삼역, 삼성역), 2);
    }

    @Test
    void removeLineSection_하행_종점역이_제거될_경우_이전_역이_하행_종점역이_된다() {
        // given
        지하철_노선에_지하철역_등록되어_있음(이호선, 강남역, 역삼역, 3);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(이호선, 삼성역);

        // then
        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        지하철_노선에_지하철역_제거됨(response);
        지하철_노선에_지하철역_모두_정렬됨(lineResponse, Arrays.asList(강남역, 역삼역), 2);
    }
}
