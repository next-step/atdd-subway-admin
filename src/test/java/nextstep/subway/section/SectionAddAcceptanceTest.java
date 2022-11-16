package nextstep.subway.section;

import com.google.common.collect.Lists;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.SubwayAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTestAssertions.역_목록_일치함;
import static nextstep.subway.line.LineAcceptanceTestAssured.*;
import static nextstep.subway.section.SectionAcceptanceTestAssured.구간_등록;
import static nextstep.subway.section.SectionAcceptanceTestAssured.구간_제거;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;

@DisplayName("구간 관련 기능")
class SectionAddAcceptanceTest extends SubwayAcceptanceTest {

    long 노선_식별자;
    long 상행종점역_식별자;
    long 하행종점역_식별자;
    long 중간역1_식별자;
    long 중간역2_식별자;

    LinkedList<String> 지하철역;
    
    int 종점간_거리 = 6;

    /**
     * 충정로-(1)-홍대-(2)-합정-(3)-당산
     */
    @BeforeEach
    void 노선_등록() {
        지하철역 = Lists.newLinkedList(Arrays.asList("충정로", "홍대", "합정", "당산"));
        상행종점역_식별자 = 지하철역_식별자(지하철역_생성(지하철역.getFirst()));
        하행종점역_식별자 = 지하철역_식별자(지하철역_생성(지하철역.getLast()));
        중간역1_식별자 = 지하철역_식별자(지하철역_생성(지하철역.get(1)));
        중간역2_식별자 = 지하철역_식별자(지하철역_생성(지하철역.get(2)));

        노선_식별자 = 지하철_노선_식별자(지하철_노선_생성("2호선", 상행종점역_식별자, 하행종점역_식별자, 종점간_거리));
        구간_등록(노선_식별자, 상행종점역_식별자, 중간역1_식별자, 1);
        구간_등록(노선_식별자, 중간역1_식별자, 중간역2_식별자, 2);
    }

    /**
     * Given 노선에 구간이 3개 존재하고
     * When 하행 종점역을 제거할 경우
     * Then 다음으로 오던 하행역이 하행종점역이 된다
     */
    @Test
    void 하행_종점역_제거() {
        구간_제거(노선_식별자, 하행종점역_식별자);

        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회(노선_식별자);
        List<String> 역_목록 = Lists.newArrayList(지하철역.get(0), 지하철역.get(1), 지하철역.get(2));
        역_목록_일치함(노선_조회_응답, 역_목록);
    }

    /**
     * Given 노선에 구간이 3개 존재하고
     * When 상행 종점역을 제거할 경우
     * Then 다음으로 오던 상행역이 상행종점역이 된다
     */
    @Test
    void 상행_종점역_제거() {
        구간_제거(노선_식별자, 상행종점역_식별자);

        ExtractableResponse<Response> 노선_조회_응답 = 지하철_노선_조회(노선_식별자);
        List<String> 역_목록 = Lists.newArrayList(지하철역.get(1), 지하철역.get(2), 지하철역.getLast());
        역_목록_일치함(노선_조회_응답, 역_목록);
    }

    /**
     * Given 노선에 구간이 3개 존재하고
     * When 가운데 역을 제거할 경우
     * Then 역이 재배치된다
     */
    @Test
    void 가운데역_제거() {
    }

    /**
     * Given 노선에 구간이 3개 존재하고
     * When 가운데 역을 제거할 경우
     * Then 거리는 두 구간의 거리의 합으로 정함
     */
    @Test
    void 제거된_역_사이의_거리() {
    }

    /**
     * Given 노선에 구간이 1개 존재하고
     * When 역을 제거할 할 경우
     * Then 역을 제거할 수 없다
     */
    @Test
    void 구간이_하나인_노선에서_역제거() {
    }


    /**
     * Given 노선에 구간이 3개 존재하고
     * When 노선에 등록되어있지 않은 역을 제거할 경우
     * Then 역을 제거할 수 없다
     */
    @Test
    void 노선에_없는_역을_제거() {
    }

}
