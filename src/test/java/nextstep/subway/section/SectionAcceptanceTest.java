package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.LineAcceptanceTest.getStationId;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long 서울역;
    private long 안산역;
    private long 사당역;
    private long 의정부역;
    private long 오이도역;
    private String 사호선;

    @BeforeEach
    public void setup() {
        서울역 = getStationId("서울역");
        안산역 = getStationId("안산역");
        사당역 = getStationId("사당역");
        의정부역 = getStationId("의정부역");
        오이도역 = getStationId("오이도역");
        사호선 = 지하철_노선_등록되어_있음(new LineRequest("4호선", "blue", 서울역, 안산역, 10)).split("/")[2];
    }

    @DisplayName("상행 역 다음 위치에 지하철 구간을 추가한다. - 상행 역 기준 추가")
    @Test
    void addSectionUpStationAfter() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 사당역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
    }

    @DisplayName("새로운 상행 역 종점으로 시작되는 지하철 구간을 추가한다.")
    @Test
    void addSectionUpStationBefore() {
        //given
        SectionRequest sectionRequest = new SectionRequest(의정부역, 서울역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
    }

    @DisplayName("하행 역 이전 위치에 지하철 구간을 추가한다. - 하행 역 기준")
    @Test
    void addSectionDownStationBefore() {
        //given
        SectionRequest sectionRequest = new SectionRequest(사당역, 안산역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
    }

    @DisplayName("하행 역 다음 위치에 새로운 지하철 종점 구간을 추가한다.")
    @Test
    void addSectionDownStationAfter() {
        //given
        SectionRequest sectionRequest = new SectionRequest(안산역, 오이도역, 50);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가됨(response);
    }

    @DisplayName("지하철 구간을 추가 실패 - 이미 등록 된 구간 추가.")
    @Test
    void alreadyAddedSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 안산역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    @DisplayName("지하철 구간을 추가 실패 - 추가 하려는 구간이 크거나 같을 경우")
    @Test
    void longDistanceSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(의정부역, 오이도역, 100);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    @DisplayName("지하철 구간을 추가 실패 - 추가되지 않은 두 역을 새로운 구간으로 추가 할 경우")
    @Test
    void notExistsSectionAddFail() {
        //given
        SectionRequest sectionRequest = new SectionRequest(서울역, 사당역, 10);

        //when
        ExtractableResponse<Response> response = 지하철_구간_추가(사호선, sectionRequest);

        //then
        지하철_구간_추가_실패(response);
    }

    public static ExtractableResponse<Response> 지하철_구간_추가(String lineId, SectionRequest section) {
        return RestAssured.given().log().all()
                .body(section)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_구간_추가_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
