package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.line.LineAcceptanceTest.신분당선_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@Sql("classpath:/createlinestation.sql")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 두 역 사이에 새로운 역을 등록하면
     * Then 새로운 역이 등록되고 구간이 조정된다.
     */
    @DisplayName("역 사이에 새로운 구간을 등록한다.")
    @Test
    void addSection() {
        // Given
        신분당선_생성();

        // When
        SectionRequest sectionRequest = new SectionRequest(1L, 5L, 4);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(1L, sectionRequest);

        // Then
        final Integer 강남역_신규구간_거리 = 4;
        final Integer 신규구간_양재역_거리 = 6;

        Integer 강남역_신규구간_거리_리턴값 = 구간_추가_결과_응답.jsonPath()
                .get("sections.find { section -> (section.upStation != null && section.upStation.id == 1L) }.distance");
        assertThat(강남역_신규구간_거리_리턴값).isEqualTo(강남역_신규구간_거리);

        Integer 신규구간_양재역_거리_리턴값 = 구간_추가_결과_응답.jsonPath()
                .get("sections.find { section -> (section.downStation != null && section.downStation.id == 2L) }.distance");
        assertThat(신규구간_양재역_거리_리턴값).isEqualTo(신규구간_양재역_거리);
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 새로운 역이 등록되고 구간이 조정된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addStationAsUpStation() {
        // Given
        신분당선_생성();

        // When
        SectionRequest sectionRequest = new SectionRequest(6L, 1L, 4);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(1L, sectionRequest);

        // Then
        final Integer 구간_추가후_신분당선_총_거리 = 14;
        final String 구간_추가후_신분당선_상행역명 = "신논현역";

        Integer 신분당선_총_거리_리턴값 = 구간_추가_결과_응답.jsonPath().get("distance");
        assertThat(신분당선_총_거리_리턴값).isEqualTo(구간_추가후_신분당선_총_거리);

        String 신분당선_상행역명_리턴값 = 구간_추가_결과_응답.jsonPath().get("upStation.name");
        assertThat(신분당선_상행역명_리턴값).isEqualTo(구간_추가후_신분당선_상행역명);
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 새로운 역이 등록되고 구간이 조정된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addStationAsDownStation() {
        // Given
        신분당선_생성();

        // When
        SectionRequest sectionRequest = new SectionRequest(2L, 5L, 7);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(1L, sectionRequest);

        // Then
        final Integer 구간_추가후_신분당선_총_거리 = 17;
        final String 구간_추가후_신분당선_하행역명 = "양재시민의숲역";

        Integer 신분당선_총_거리_리턴값 = 구간_추가_결과_응답.jsonPath().get("distance");
        assertThat(신분당선_총_거리_리턴값).isEqualTo(구간_추가후_신분당선_총_거리);

        String 신분당선_상행역명_리턴값 = 구간_추가_결과_응답.jsonPath().get("downStation.name");
        assertThat(신분당선_상행역명_리턴값).isEqualTo(구간_추가후_신분당선_하행역명);
    }

    /**
     * Given 지하철 노선이 생성된 상태에서
     * When 두 역 사이에 현재 노선보다 긴 구간의 역을 등록하면
     * Then 오류가 발생한다.
     */
    @DisplayName("기존 역 사이보다 긴 구간의 길이")
    @Test
    void longerThanExistingSection() {
        // Given
        신분당선_생성();

        // When
        SectionRequest sectionRequest = new SectionRequest(1L, 5L, 20);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(1L, sectionRequest);

        // Then
        assertThat(구간_추가_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 생성을 생성하고 두 개의 구간을 등록한 후
     * When 등록된 구간에 포함된 역을 구간으로 등록하면
     * Then 오류가 발생한다.
     */
    @DisplayName("이미 등록된 역으로 구성된 구간 등록")
    @Test
    void addSectionWithExistingStation() {
        // Given
        신분당선_생성();
        SectionRequest sectionRequest = new SectionRequest(2L, 5L, 10);
        구간_추가(1L, sectionRequest);

        // When
        SectionRequest badSectionRequest = new SectionRequest(1L, 5L, 5);
        ExtractableResponse<Response> 구간_추가_결과_응답 = 구간_추가(1L, badSectionRequest);

        // Then
        assertThat(구간_추가_결과_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
