package nextstep.subway.linestation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("노선에 구간을 등록한다.")
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

    private ExtractableResponse<Response> 구간_추가(long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
