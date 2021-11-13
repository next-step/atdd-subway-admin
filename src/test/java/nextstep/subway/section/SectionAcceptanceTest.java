package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.section.SectionAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선에 구간 추가 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final int DISTANCE = 10;

    private StationResponse 판교역;
    private StationResponse 미금역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        판교역 = 지하철_역_등록되어_있음(StationRequest.from("판교역")).as(StationResponse.class);
        미금역 = 지하철_역_등록되어_있음(StationRequest.from("미금역")).as(StationResponse.class);

        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   판교역.getId(),
                                                   미금역.getId(),
                                                   DISTANCE);
        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("노선 중간에 새로운 역을 등록한다.")
    @Test
    void addSection1() {
        // given
        StationResponse 정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest 판교_정자_구간 = SectionRequest.of(판교역.getId(), 정자역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 판교_정자_구간);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(신분당선.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(판교역.getId(), 정자역.getId(), 미금역.getId()));
    }

    @DisplayName("노선에 새로운 상행 종점역을 등록한다.")
    @Test
    void addSection2() {
        // given
        StationResponse 정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest 정자_판교_구간 = SectionRequest.of(정자역.getId(), 판교역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 정자_판교_구간);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(신분당선.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(정자역.getId(), 판교역.getId(), 미금역.getId()));
    }

    @DisplayName("노선에 새로운 하행 종점역을 등록한다.")
    @Test
    void addSection3() {
        // given
        StationResponse 정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest 미금_정자_구간 = SectionRequest.of(미금역.getId(), 정자역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 미금_정자_구간);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(신분당선.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(판교역.getId(), 미금역.getId(), 정자역.getId()));
    }

    @DisplayName("노선 중간에 새로운 역을 등록하는 경우, 기존 구간 길이보다 크거나 같으면 등록할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 50, 100})
    void addSection4(int distance) {
        // given
        StationResponse 정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest 판교_정자_구간 = SectionRequest.of(판교역.getId(), 정자역.getId(), distance);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 판교_정자_구간);

        // then
        지하철_구간_등록_실패(response);
    }

    @DisplayName("신규 구간의 상행역과 하행역이 이미 노선에 존재하면 추가할 수 없다.")
    @Test
    void addSection5() {
        // given
        SectionRequest 판교_미금_구간 = SectionRequest.of(판교역.getId(), 미금역.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 판교_미금_구간);

        // then
        지하철_구간_등록_실패(response);
    }

    @DisplayName("신규 구간의 상행역과 하행역이 노선에 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addSection6() {
        // given
        StationResponse 정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        StationResponse 동천역 = 지하철_역_생성_요청(StationRequest.from("동천역")).as(StationResponse.class);
        SectionRequest 정자_동천_구간 = SectionRequest.of(정자역.getId(), 동천역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 정자_동천_구간);

        // then
        지하철_구간_등록_실패(response);
    }
}
