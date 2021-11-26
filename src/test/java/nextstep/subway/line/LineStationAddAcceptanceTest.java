package nextstep.subway.line;

import com.google.common.collect.Lists;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.StationAcceptanceTestUtil.*;
import static nextstep.subway.utils.LineStationAcceptanceTestUtil.*;
import static nextstep.subway.utils.LineAcceptanceTestUtil.*;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineStationAddAcceptanceTest extends AcceptanceTest {

    private Long 노선ID;
    private Long 잠실역ID;
    private Long 몽촌토성역ID;
    private Long 강동구청역ID;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        잠실역ID = 지하철됨_역_생성_됨_toId("잠실역");
        몽촌토성역ID = 지하철됨_역_생성_됨_toId("몽촌토성역");
        강동구청역ID = 지하철됨_역_생성_됨_toId("강동구청역");

        ExtractableResponse<Response> createLineResponse = 지하철_노선_등록되어_있음("2호선", "RED", 잠실역ID,
            몽촌토성역ID, 100);

        노선ID = createLineResponse.as(LineResponse.class).getId();
    }


    @DisplayName("기존 구간 사이에 역추가하기")
    @Test
    void 기존_구간_사이에_역추가() {
        // given
        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = 지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 강동구청역ID, 50);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.CREATED);
    }


    @DisplayName("지하철 노선에 여러개의 역을 순서대로 등록한다.")
    @Test
    void 지하철노선_노회_역_정렬_등록() {
        // given
        지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 강동구청역ID, 50);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(잠실역ID, 강동구청역ID, 몽촌토성역ID));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록 한다.")
    @Test
    void 상행_종점_역_등록() {
        // given
        지하철_노선구간_상행_종점_역_추가_되어_있음(노선ID, 강동구청역ID, 50);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(강동구청역ID, 잠실역ID, 몽촌토성역ID));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록 한다.")
    @Test
    void 하행_종점_역_등록() {
        // given
        지하철_노선구간_하행_종점_역_추가_되어_있음(노선ID, 강동구청역ID, 50);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(잠실역ID, 몽촌토성역ID, 강동구청역ID));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이와 같으면 등록을 할 수 없음")
    @Test
    void 추가_역길이_기존_역사이_길이와_같으면_실패() {
        // given
        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = 지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 강동구청역ID, 100);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음")
    @Test
    void 추가_역길이_기존_역사이_길이_이상이면_실패() {
        // given
        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = 지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 강동구청역ID, 200);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행_하행_이미_모두_등록시_실패() {
        // given
        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = 지하철_노선구간_사이에_역_추가_되어_있음(노선ID, 잠실역ID, 200);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 상행_하행_일치하는_역_없으면_실패() {
        // given
        long 없는역ID1 = 지하철됨_역_생성_됨_toId("없는역1");
        long 없는역ID2 = 지하철됨_역_생성_됨_toId("없는역2");

        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = 지하철_노선구간_추가_되어_있음(노선ID, 없는역ID1, 없는역ID2, 200);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.BAD_REQUEST);
    }


    private ExtractableResponse<Response> 지하철_노선구간_사이에_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, stationId, 몽촌토성역ID, distance);
    }

    private ExtractableResponse<Response> 지하철_노선구간_상행_종점_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, stationId, 잠실역ID, distance);
    }

    private ExtractableResponse<Response> 지하철_노선구간_하행_종점_역_추가_되어_있음(Long lineId, Long stationId,
        int distance) {
        return 지하철_노선구간_추가_되어_있음(lineId, 몽촌토성역ID, stationId, distance);
    }
}
