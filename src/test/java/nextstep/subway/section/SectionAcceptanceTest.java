package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineAcceptanceMethod.*;
import static nextstep.subway.section.SectionAcceptanceMethod.*;
import static nextstep.subway.station.StationAcceptanceMethod.*;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    private Long 강남역_ID;
    private Long 판교역_ID;
    private Long 새로운역_ID;
    private ExtractableResponse<Response> 신분당선_생성_응답;

    @BeforeEach
    void createLine() {
        // given
        this.강남역_ID = 지하철역_생성("강남역").jsonPath().getLong(idKey);
        this.판교역_ID = 지하철역_생성("판교역").jsonPath().getLong(idKey);
        this.새로운역_ID = 지하철역_생성("새로운역").jsonPath().getLong(idKey);
        LineRequest 신분당선_생성_요청 = LineRequest.of("신분당선", lineColorRed, this.강남역_ID, this.판교역_ID, distance);
        this.신분당선_생성_응답 = 지하철노선_생성(신분당선_생성_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 지하철 구간에 추가하면
     * Then 새로운 역이 구간에 추가되고
     * Then 노선에 추가된 새로운 역 조회할 수 있다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSection() {
        // when
        SectionRequest 새로운역_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 4);
        ExtractableResponse<Response> 새로운역_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // then
        지하철구간_추가됨(새로운역_추가_응답, 새로운역_구간_요청);

        // then
        추가된_지하철구간_조회(this.신분당선_생성_응답, 새로운역_구간_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 상행 종점을 등록하면
     * Then 새로운 상행 종점이 구간에 추가되고
     * Then 노선에 추가된 새로운 상행 종점을 조회할 수 있다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void newUpStation() {
        // when
        SectionRequest 새로운역_상행종점_요청 = SectionRequest.of(this.새로운역_ID, this.강남역_ID, 4);
        ExtractableResponse<Response> 새로운역_상행종점_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_상행종점_요청);

        // then
        지하철구간_추가됨(새로운역_상행종점_추가_응답, 새로운역_상행종점_요청);

        // then
        추가된_지하철구간_조회(this.신분당선_생성_응답, 새로운역_상행종점_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 하행 종점을 등록하면
     * Then 새로운 하행 종점이 구간에 추가되고
     * Then 노선에 추가된 새로운 하행 종점을 조회할 수 있다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void newDownStation() {
        // when
        SectionRequest 새로운역_하행종점_요청 = SectionRequest.of(this.판교역_ID, this.새로운역_ID, 4);
        ExtractableResponse<Response> 새로운역_하행종점_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_하행종점_요청);

        // then
        지하철구간_추가됨(새로운역_하행종점_추가_응답, 새로운역_하행종점_요청);

        // then
        추가된_지하철구간_조회(this.신분당선_생성_응답, 새로운역_하행종점_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 역 사이 길이보다 큰 길이의 새로운 구간을 등록하면
     * Then 새로운 구간을 등록할 수 없다
     */
    @DisplayName("기존 역 사이 길이보다 크면 등록할 수 없다.")
    @Test
    void greaterThenDistance() {
        // when
        SectionRequest 길이가_더_큰_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 11);
        ExtractableResponse<Response> 길이가_더_큰_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 길이가_더_큰_구간_요청);

        // then
        새로운_구간_추가_안됨(길이가_더_큰_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 역 사이 길이와 같은 새로운 구간을 등록하면
     * Then 새로운 구간을 등록할 수 없다
     */
    @DisplayName("기존 역 사이 길이와 같으면 등록할 수 없다.")
    @Test
    void equalsDistance() {
        // when
        SectionRequest 길이가_같은_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 10);
        ExtractableResponse<Response> 길이가_같은_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 길이가_같은_구간_요청);

        // then
        새로운_구간_추가_안됨(길이가_같은_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 구간의 상/하행역이 모두 기존 노선에 있으면
     * Then 새로운 구간을 등록할 수 없다
     */
    @DisplayName("상/하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void existAllStations() {
        // when
        SectionRequest 기존_노선에_있는_구간_요청 = SectionRequest.of(this.강남역_ID, this.판교역_ID, 4);
        ExtractableResponse<Response> 기존_노선에_있는_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 기존_노선에_있는_구간_요청);

        // then
        새로운_구간_추가_안됨(기존_노선에_있는_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 구간의 상/하행역이 모두 기존 노선에 없으면
     * Then 새로운 구간을 등록할 수 없다
     */
    @DisplayName("상/하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    void notExistAllStations() {
        // when
        Long 없는역_ID = 지하철역_생성("없는역").jsonPath().getLong(idKey);
        SectionRequest 기존_노선에_없는_구간_요청 = SectionRequest.of(this.새로운역_ID, 없는역_ID, 4);
        ExtractableResponse<Response> 기존_노선에_없는_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 기존_노선에_없는_구간_요청);

        // then
        새로운_구간_추가_안됨(기존_노선에_없는_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하여
     * Given 기존 역 사이에 지하철 구간을 추가하고
     * When 기존 상행 종점 구간을 제거하면
     * Then 추가한 지하철역을 새로운 상행 종점으로 조회할 수 있다
     */
    @DisplayName("상행 종점을 제거하고 새로운 상행 종점을 조회한다.")
    @Test
    void deleteEdgeUpSection() {
        // given
        SectionRequest 새로운역_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 4);
        지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // when
        지하철_구간_제거(this.신분당선_생성_응답, this.강남역_ID);

        // then
        새로운_종점_조회(this.신분당선_생성_응답, this.새로운역_ID);
    }

    /**
     * Given 지하철 노선을 생성하여
     * Given 기존 역 사이에 지하철 구간을 추가하고
     * When 기존 하행 종점 구간을 제거하면
     * Then 추가한 지하철역을 새로운 하행 종점으로 조회할 수 있다
     */
    @DisplayName("하행 종점을 제거하고 새로운 하행 종점을 조회한다.")
    @Test
    void deleteEdgeDownSection() {
        // given
        SectionRequest 새로운역_구간_요청 = SectionRequest.of(this.새로운역_ID, this.판교역_ID, 4);
        지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // when
        지하철_구간_제거(this.신분당선_생성_응답, this.판교역_ID);

        // then
        새로운_종점_조회(this.신분당선_생성_응답, this.새로운역_ID);
    }

    /**
     * Given 지하철 노선을 생성하여
     * Given 기존 구간 사이에 새로운 역을 추가하고
     * When 추가한 역을 구간에서 제거하면
     * Then 추가한 역이 구간에서 제거되고 노선에서 조회되지 않는다
     */
    @DisplayName("기존 구간 사이에 추가된 역을 제거한다.")
    @Test
    void deleteSection() {
        // given
        SectionRequest 새로운역_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 4);
        ExtractableResponse<Response> 새로운역_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // when
        지하철_구간_제거(this.신분당선_생성_응답, this.새로운역_ID);

        // then
        제거된역_조회_안됨(this.신분당선_생성_응답, this.새로운역_ID);
    }

    /**
     * Given 지하철 노선을 생성하여
     * Given 기존 역 사이에 지하철 구간을 추가하고
     * When 노선에 등록되어 있지 않은 역을 구간에서 제거하려고 하면
     * Then 지하철역을 제거할 수 없다
     */
    @DisplayName("노선에 등록되어 있지 않은 역은 구간에서 제거할 수 없다.")
    @Test
    void deleteNotExistSection() {
        // given
        SectionRequest 새로운역_구간_요청 = SectionRequest.of(this.강남역_ID, this.새로운역_ID, 4);
        ExtractableResponse<Response> 새로운역_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // when
        Long 없는역_ID = 지하철역_생성("없는역").jsonPath().getLong(idKey);
        ExtractableResponse<Response> 새로운역_제거_응답 = 지하철_구간_제거(this.신분당선_생성_응답, 없는역_ID);

        // then
        지하철_구간_제거_안됨(새로운역_제거_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행 종점을 구간에서 제거하려고 하면
     * Then 지하철역을 제거할 수 없다
     */
    @DisplayName("구간이 하나인 노선에서 상행 종점은 제거할 수 없다.")
    @Test
    void deleteOnlyOneEdgeUpSection() {
        // when
        ExtractableResponse<Response> 상행종점_제거_응답 = 지하철_구간_제거(this.신분당선_생성_응답, this.강남역_ID);

        // then
        지하철_구간_제거_안됨(상행종점_제거_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행 종점을 구간에서 제거하려고 하면
     * Then 지하철역을 제거할 수 없다
     */
    @DisplayName("구간이 하나인 노선에서 하행 종점은 제거할 수 없다.")
    @Test
    void deleteOnlyOneEdgeDownSection() {
        // when
        ExtractableResponse<Response> 하행종점_제거_응답 = 지하철_구간_제거(this.신분당선_생성_응답, this.판교역_ID);

        // then
        지하철_구간_제거_안됨(하행종점_제거_응답);
    }
}
