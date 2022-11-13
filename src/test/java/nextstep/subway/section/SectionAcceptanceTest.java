package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.SubwayAcceptanceTest;
import nextstep.subway.line.LineAcceptanceTestRequest;
import nextstep.subway.line.LineAcceptanceTestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_조회;
import static nextstep.subway.section.SectionAcceptanceTestAssertions.*;
import static nextstep.subway.section.SectionAcceptanceTestAssured.구간_등록;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;

@DisplayName("구간 관련 기능")
class SectionAcceptanceTest extends SubwayAcceptanceTest {

    LineAcceptanceTestRequest 노선요청정보;
    LineAcceptanceTestResponse 노선응답정보;

    @BeforeEach
    void 노선_등록() {
        노선요청정보 = new LineAcceptanceTestRequest("2호선", "합정역", "홍대역", 10);
        노선응답정보 = 지하철_노선_생성(노선요청정보);
    }

    /**
     * Given 하행 종점역과 상행 종점역이 생성되어 있고
     * When 노선 등록시 종점역 간의 거리를 전달할 경우
     * Then 구간이 함께 등록된다
     */
    @Test
    void 노선_등록시_구간이_함께_등록된다() {
        ExtractableResponse<Response> 조회_응답 = 지하철_노선_조회(노선응답정보.노선_식별자);

        구간_등록됨(조회_응답);
        구간_거리_등록됨(조회_응답, 노선요청정보.거리);
    }

    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 기존 상행 종점역을 상행역으로 하고, 새로운 역을 하행역으로 하는 구간을 등록하며
     * When 기존 노선의 길이보다 작은 구간의 길이가 등록할 경우
     * Then 역과 역사이의 새로운 역이 등록된다.
     * Then 기존의 역과 새롭게 추가된 역 사이의 양쪽 길이는 기존 구간의 길이의 합과 같다.
     */
    @Test
    void 역_사이에_새로운_역을_등록한다() {
        long 하행역_식별자 = 지하철역_식별자(지하철역_생성("가양역"));
        int 기존_구간거리 = 노선요청정보.거리;
        int 거리 = 5;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 노선응답정보.상행종점역_식별자, 하행역_식별자, 거리);
        구간_등록(구간요청정보);

        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회(노선응답정보.노선_식별자);
        구간_등록됨(노선_조회_응답);
        총_구간_거리_합이_같음(노선_조회_응답, 기존_구간거리);
    }

    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 새로운 역을 상행역으로 하고, 상행 종점역을 하행역으로 하는 구간을 등록하면
     * Then 새로운 역을 상행 종점역으로 등록할 수 있다.
     */
    @Test
    void 새로운_역을_상행_종점으로_등록한다() {
        long 상행역_식별자 = 지하철역_식별자(지하철역_생성("가양역"));
        int 기존_구간거리 = 노선요청정보.거리;
        int 거리 = 5;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 상행역_식별자, 노선응답정보.상행종점역_식별자, 거리);
        구간_등록(구간요청정보);

        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회(노선응답정보.노선_식별자);
        구간_등록됨(노선_조회_응답);
        총_구간_거리_합이_같음(노선_조회_응답, 기존_구간거리);
    }


    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 새로운 역을 하행역으로 하고, 하행 종점역을 상행역으로 하는 구간을 등록하면
     * Then 새로운 역을 하행 종점역으로 등록할 수 있다.
     */
    @Test
    void 새로운_역을_하행_종점으로_등록한다() {
        long 하행역_종점역_식별자 = 지하철역_식별자(지하철역_생성("가양역"));
        int 기존_구간거리 = 노선요청정보.거리;
        int 거리 = 기존_구간거리 - 1;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 노선응답정보.하행종점역_식별자, 하행역_종점역_식별자, 거리);
        구간_등록(구간요청정보);

        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회(노선응답정보.노선_식별자);
        구간_등록됨(노선_조회_응답);
        총_구간_거리_합이_같음(노선_조회_응답, 기존_구간거리);
    }

    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 구간의 길이보다 같거나 큰 길이의 구간을 사이에 등록할 경우
     * Then 구간 등록을 할 수 없다.
     */
    @Test
    void 역_사이에_구간_등록시_역_사이_길이보다_크거나_같으면_구간을_등록할_수_없다() {
        long 새로운_하행역_식별자 = 지하철역_식별자(지하철역_생성("가양역"));
        int 거리 = 노선요청정보.거리 + 1;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 노선응답정보.상행종점역_식별자, 새로운_하행역_식별자, 거리);
        ExtractableResponse<Response> 응답 = 구간_등록(구간요청정보);

        구간_등록_실패함(응답);
    }

    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 해당 구간의 상행역 및 하행역이 모두 새로운 구간의 상행역 및 하행역과 일치할 경우
     * Then 구간 등록을 할 수 없다.
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없다() {
        long 상행역_식별자 = 노선응답정보.상행종점역_식별자;
        long 하행역_식별자 = 노선응답정보.하행종점역_식별자;
        int 거리 = 5;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 상행역_식별자, 하행역_식별자, 거리);
        ExtractableResponse<Response> 응답 = 구간_등록(구간요청정보);

        구간_등록_실패함(응답);
    }

    /**
     * Given 상행 종점역과 하행 종점역을 갖는 노선과 구간을 등록하고
     * When 해당 구간의 상행역 혹은 하행역이 없는 다른 구간을 등록할 경우
     * Then 구간 등록을 할 수 없다.
     */
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없다() {
        long 새로운_상행역_식별자 = 지하철역_식별자(지하철역_생성("가양역"));
        long 새로운_하행역_식별자 = 지하철역_식별자(지하철역_생성("여의도역"));
        int 거리 = 5;

        SectionAcceptanceTestRequest 구간요청정보
                = new SectionAcceptanceTestRequest(노선응답정보.노선_식별자, 새로운_상행역_식별자, 새로운_하행역_식별자, 거리);
        ExtractableResponse<Response> 응답 = 구간_등록(구간요청정보);

        구간_등록_실패함(응답);
    }

}
