package nextstep.subway.line;

import com.google.common.collect.Lists;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LineAcceptanceTestUtil;
import nextstep.subway.utils.StationAcceptanceTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.LineStationAcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

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
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "잠실역");
        ExtractableResponse<Response> createdStationResponse2 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "몽촌토성역");
        ExtractableResponse<Response> createdStationResponse3 = StationAcceptanceTestUtil.지하철됨_역_생성_됨(
            "강동구청역");

        잠실역ID = createdStationResponse1.as(StationResponse.class).getId();
        몽촌토성역ID = createdStationResponse2.as(StationResponse.class).getId();
        강동구청역ID = createdStationResponse3.as(StationResponse.class).getId();

        ExtractableResponse<Response> createLineResponse = LineAcceptanceTestUtil.지하철_노선_등록되어_있음(
            "2호선", "RED", 잠실역ID, 몽촌토성역ID, 100);

        노선ID = createLineResponse.as(LineResponse.class).getId();
    }


    @DisplayName("기존 구간 사이에 역추가하기")
    @Test
    void 기존_구간_사이에_역추가() {
        // given
        // when
        ExtractableResponse<Response> 노선_구간_추가_응답 = LineAcceptanceTestUtil.지하철_노선구간_추가_되어_있음(노선ID,
            잠실역ID, 강동구청역ID, 50);

        // then
        지하철_구간_요청_응답_검증(노선_구간_추가_응답, HttpStatus.CREATED);
    }

    @DisplayName("지하철 노선에 여러개의 역을 순서대로 등록한다.")
    @Test
    void 지하철노선_노회_역_정렬_등록() {
        // given
        LineAcceptanceTestUtil.지하철_노선구간_추가_되어_있음(노선ID, 강동구청역ID, 몽촌토성역ID, 50);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(잠실역ID, 강동구청역ID, 몽촌토성역ID));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록 한다.")
    @Test
    void 상행_종점_역_등록() {
        // given
        LineAcceptanceTestUtil.지하철_노선구간_추가_되어_있음(노선ID, 강동구청역ID, 잠실역ID, 50);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(강동구청역ID, 잠실역ID, 몽촌토성역ID));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록 한다.")
    @Test
    void 하행_종점_역_등록() {
        // given
        LineAcceptanceTestUtil.지하철_노선구간_추가_되어_있음(노선ID, 몽촌토성역ID, 강동구청역ID, 50);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtil.지하철_노선_조회_요청(노선ID);

        // then
        지하철_구간_요청_응답_검증(response, HttpStatus.OK);
        지하철_노선에_지하척역_순서_정렬됨(response, Lists.newArrayList(잠실역ID, 몽촌토성역ID, 강동구청역ID));
    }

}
