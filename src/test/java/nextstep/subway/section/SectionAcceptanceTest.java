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
    public void setUp() {
        // given
        this.강남역_ID = 지하철역_생성("강남역").jsonPath().getLong(ID_KEY);
        this.판교역_ID = 지하철역_생성("판교역").jsonPath().getLong(ID_KEY);
        this.새로운역_ID = 지하철역_생성("새로운역").jsonPath().getLong(ID_KEY);
        LineRequest 신분당선_생성_요청 = LineRequest.of("신분당선", LINE_COLOR_RED, this.강남역_ID, this.판교역_ID, DISTANCE);
        this.신분당선_생성_응답 = 지하철노선_생성(신분당선_생성_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역을 지하철 구간에 추가하면
     * Then 새로운 역이 구간에 추가되고
     * Then 기존 역과 새로운 역에 설정된 구간 길이를 조회할 수 있다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSection() {
        // when
        SectionRequest 새로운역_구간_요청 = null;
        ExtractableResponse<Response> 새로운역_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_구간_요청);

        // then
        추가된_지하철구간_확인(새로운역_추가_응답);

        // then
        추가된_지하철구간_길이_조회(새로운역_구간_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 상행 종점을 등록하면
     * Then 새로운 상행 종점이 구간에 추가되고
     * Then 이전 상행 종점과의 구간 길이를 조회할 수 있다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void newUpStation() {
        // when
        SectionRequest 새로운역_상행종점_요청 = null;
        ExtractableResponse<Response> 새로운역_상행종점_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_상행종점_요청);

        // then
        추가된_지하철구간_확인(새로운역_상행종점_추가_응답);

        // then
        추가된_지하철구간_길이_조회(새로운역_상행종점_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 하행 종점을 등록하면
     * Then 새로운 하행 종점이 구간에 추가되고
     * Then 이전 하행 종점과의 구간 길이를 조회할 수 있다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void newDownStation() {
        // when
        SectionRequest 새로운역_하행종점_요청 = null;
        ExtractableResponse<Response> 새로운역_하행종점_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 새로운역_하행종점_요청);

        // then
        추가된_지하철구간_확인(새로운역_하행종점_추가_응답);

        // then
        추가된_지하철구간_길이_조회(새로운역_하행종점_요청);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 역 사이 길이보다 큰 길이의 새로운 역을 등록하면
     * Then 새로운 역을 등록할 수 없다
     */
    @DisplayName("기존 역 사이 길이보다 크면 등록할 수 없다.")
    @Test
    void greaterThenDistance() {
        // when
        SectionRequest 길이가_더_큰_구간_요청 = null;
        ExtractableResponse<Response> 길이가_더_큰_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 길이가_더_큰_구간_요청);

        // then
        새로운역_추가_안됨(길이가_더_큰_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존 역 사이 길이와 같은 새로운 역을 등록하면
     * Then 새로운 역을 등록할 수 없다
     */
    @DisplayName("기존 역 사이 길이와 같으면 등록할 수 없다.")
    @Test
    void equalsDistance() {
        // when
        SectionRequest 길이가_같은_구간_요청 = null;
        ExtractableResponse<Response> 길이가_같은_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 길이가_같은_구간_요청);

        // then
        새로운역_추가_안됨(길이가_같은_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역의 상/하행역이 모두 기존 노선에 있으면
     * Then 새로운 역을 등록할 수 없다
     */
    @DisplayName("상/하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void existAllStations() {
        // when
        SectionRequest 기존_노선에_있는_구간_요청 = null;
        ExtractableResponse<Response> 기존_노선에_있는_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 기존_노선에_있는_구간_요청);

        // then
        새로운역_추가_안됨(기존_노선에_있는_구간_추가_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 새로운 역의 상/하행역이 모두 기존 노선에 없으면
     * Then 새로운 역을 등록할 수 없다
     */
    @DisplayName("상/하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없다.")
    @Test
    void notExistAllStations() {
        // when
        SectionRequest 기존_노선에_없는_구간_요청 = null;
        ExtractableResponse<Response> 기존_노선에_없는_구간_추가_응답 = 지하철_구간_추가(this.신분당선_생성_응답, 기존_노선에_없는_구간_요청);

        // then
        새로운역_추가_안됨(기존_노선에_없는_구간_추가_응답);
    }
}
