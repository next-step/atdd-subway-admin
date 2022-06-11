package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.노선_조회;
import static nextstep.subway.line.LineAcceptanceTest.신분당선_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@Sql("classpath:/createlinestation.sql")
class SectionAcceptanceTest extends BaseAcceptanceTest {


    private static final long 강남역_ID = 1L;
    private static final long 양재역_ID = 2L;
    private static final long 신림역_ID = 3L;
    private static final long 서원역_ID = 4L;
    private static final long 양재시민의숲역_ID = 5L;
    private static final long 신논현역_ID = 6L;
    private static final long 신분당선_ID = 1L;

    @BeforeEach
    void createLine() {
        신분당선_생성();
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 두 역 사이에 상행역에서 출발하는 새로운 역을 등록하면
     * Then 두 역 사이에 새로운 역이 등록된다.
     */
    @DisplayName("역 사이에 상행역에서 출발하는 새로운 구간을 등록한다.")
    @Test
    void addSection() {
        // When
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 양재시민의숲역_ID, 4);
        구간_추가(신분당선_ID, sectionRequest);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);
        List<String> 신분당선_역명_리턴값 = 노선_조회_결과_응답.jsonPath().getList("stations.name");
        assertThat(신분당선_역명_리턴값).containsExactly("강남역", "양재시민의숲역", "양재역");
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 두 역 사이에 하행역에서 출발하는 새로운 역을 등록하면
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("역 사이에 하행역에서 출발하는 새로운 구간을 등록한다.")
    @Test
    void addSectionFromDownStation() {
        // When
        SectionRequest sectionRequest = new SectionRequest(양재시민의숲역_ID, 양재역_ID, 4);
        구간_추가(신분당선_ID, sectionRequest);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);
        List<String> 신분당선_역명_리턴값 = 노선_조회_결과_응답.jsonPath().getList("stations.name");
        assertThat(신분당선_역명_리턴값).containsExactly("강남역", "양재시민의숲역", "양재역");
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addStationAsUpStation() {
        // When
        SectionRequest sectionRequest = new SectionRequest(신논현역_ID, 강남역_ID, 4);
        구간_추가(신분당선_ID, sectionRequest);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);

        final String 구간_추가후_신분당선_상행역명 = "신논현역";

        String 신분당선_상행역명_리턴값 = 노선_조회_결과_응답.jsonPath().get("stations[0].name");
        assertThat(신분당선_상행역명_리턴값).isEqualTo(구간_추가후_신분당선_상행역명);
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 새로운 역이 하행 종점역으로 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addStationAsDownStation() {
        // When
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 양재시민의숲역_ID, 7);
        구간_추가(신분당선_ID, sectionRequest);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);

        List<String> 신분당선_역명_리턴값 = 노선_조회_결과_응답.jsonPath().getList("stations.name");
        assertThat(신분당선_역명_리턴값).containsExactly("강남역", "양재역", "양재시민의숲역");
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 두 역 사이에 현재 노선보다 긴 구간의 역을 등록하면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 역 사이보다 긴 구간의 길이")
    @Test
    void longerThanExistingSection() {
        // When
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 양재시민의숲역_ID, 20);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(신분당선_ID, sectionRequest);

        // Then
        assertThat(구간_추가_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고 두 개의 구간을 등록한 후
     * When 등록된 구간에 포함된 역을 구간으로 등록하면
     * Then 오류가 발생한다.
     */
    @DisplayName("이미 등록된 역으로 구성된 구간 등록")
    @Test
    void addSectionWithExistingStation() {
        // Given
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 양재시민의숲역_ID, 10);
        구간_추가(신분당선_ID, sectionRequest);

        // When
        SectionRequest badSectionRequest = new SectionRequest(강남역_ID, 양재시민의숲역_ID, 5);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(신분당선_ID, badSectionRequest);

        // Then
        assertThat(구간_추가_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 등록된 구간에 포함되지 않은 두 역을 구간으로 등록하면
     * Then 오류가 발생한다.
     */
    @DisplayName("등록하지 않은 역으로 구성된 구간 등록")
    @Test
    void addSectionWithOtherStation() {
        // When
        SectionRequest badSectionRequest = new SectionRequest(신림역_ID, 서원역_ID, 5);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(신분당선_ID, badSectionRequest);

        // Then
        assertThat(구간_추가_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 노선의 종착역과 연결되는 신규 노선을 추가한 후 기존 종착역을 제거하면
     * Then 기존 종착역은 조회되지 않는다.
     */
    @DisplayName("역 사이에 상행역에서 출발하는 새로운 구간을 등록한다.")
    @Test
    void removeSection() {
        // When
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 양재시민의숲역_ID, 4);
        구간_추가(신분당선_ID, sectionRequest);
        구간_제거(신분당선_ID, 양재역_ID);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);
        List<String> 신분당선_역명_리턴값 = 노선_조회_결과_응답.jsonPath().getList("stations.name");
        assertThat(신분당선_역명_리턴값).containsExactly("강남역", "양재시민의숲역");
    }

    /**
     * Given 지하철 노선이 최초 생성된 상태에서
     * When 구간을 제거하면
     * Then 에러가 발생한다.
     */
    @DisplayName("구간이 하나만 등록된 노선의 구간을 제거한다.")
    @Test
    void removeLastOneSection() {
        // When
        ExtractableResponse<Response> 구간_제거_결과_응답 = 구간_제거(신분당선_ID, 양재역_ID);

        // Then
        assertThat(구간_제거_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 최초 생성된 상태에서
     * When 등록되지 않은 역을 제거하면
     * Then 에러가 발생한다.
     */
    @DisplayName("등록되지 않은 구간을 제거한다.")
    @Test
    void removeNotPresentSection() {
        // When
        ExtractableResponse<Response> 구간_제거_결과_응답 = 구간_제거(신분당선_ID, 신림역_ID);

        // Then
        assertThat(구간_제거_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 최초 생성된 상태에서
     * When 종점과 이어지는 구간을 추가하여 종점을 변경한 후 종점을 제거하면
     * Then 추가된 종점은 제거되고 최초 생성 상태 노선이 조회된다.
     */
    @DisplayName("종점을 변경한 후 종점을 제거한다.")
    @Test
    void removeLastStation() {
        // When
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 양재시민의숲역_ID, 4);
        구간_추가(신분당선_ID, sectionRequest);
        구간_제거(신분당선_ID, 양재시민의숲역_ID);

        // Then
        ExtractableResponse<Response> 노선_조회_결과_응답 = 노선_조회(신분당선_ID);
        List<String> 신분당선_역명_리턴값 = 노선_조회_결과_응답.jsonPath().getList("stations.name");
        assertThat(신분당선_역명_리턴값).containsExactly("강남역", "양재역");
    }

    private ExtractableResponse<Response> 구간_제거(long lineId, long stationId) {
        return RestAssured.given().log().all()
                .queryParam("stationId", stationId)
                .when().delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 구간_추가(long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
