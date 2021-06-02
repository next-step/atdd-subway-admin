package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static java.lang.String.format;
import static nextstep.subway.line.LineAcceptanceTest.라인_생성_및_체크;
import static nextstep.subway.line.LineAcceptanceTest.분당_라인;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("구간 테스트")
class SectionAcceptanceTest extends AcceptanceTest {
    private static Long 강남역_ID = 1L;
    private static Long 역삼역_ID = 2L;
    private static Long 수진역_ID = 3L;

    private static Long 분당_라인_ID = 1L;

    private static SectionRequest 역삼역_수진역_길이_15 = new SectionRequest(역삼역_ID, 수진역_ID, 15L);

    @TestFactory
    @DisplayName("신규 구간을 추가한다 (상)역삼역 <-> (하)수진역")
    Stream<DynamicTest> 신규_구간을_추가한다_상_역삼역_하_수진역() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[] {강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(역삼역_수진역_길이_15, 분당_라인_ID,2L)),
                dynamicTest("분당라인의 전체 연결을 확인한다", 전체_연결_확인(분당_라인_ID))
        );
    }

    private Executable 전체_연결_확인(Long lineId) {
        return () -> {
            ExtractableResponse<Response> response = RestAssured.given()
                    .log().all()
                    .when()
                    .log().all()
                    .get(format("/%d/sections", lineId))
                    .then()
                    .log().all()
                    .extract();

            정상_응답_헤더_검증(response);

            SectionResponse[] sectionResponses = response.as(SectionResponse[].class);

            SectionResponse 강남역_역삼역_구간 = sectionResponses[0];
            SectionResponse 역삼역_수진역_구간 = sectionResponses[1];

            assertThat(강남역_역삼역_구간.getUpStationId())
                    .isEqualTo(강남역_ID);
            assertThat(강남역_역삼역_구간.getDownStationId())
                    .isEqualTo(역삼역_ID);
            assertThat(강남역_역삼역_구간.getDistance())
                    .isEqualTo(분당_라인.getDistance());


            assertThat(역삼역_수진역_구간.getUpStationId())
                    .isEqualTo(역삼역_ID);
            assertThat(역삼역_수진역_구간.getDownStationId())
                    .isEqualTo(수진역_ID);
            assertThat(역삼역_수진역_구간.getDistance())
                    .isEqualTo(역삼역_수진역_길이_15.getDistance());
        };
    }

    private Executable 구간_생성_및_체크(SectionRequest sectionRequest, Long lineId, Long expectId) {
        return () -> {
            ExtractableResponse<Response> response = RestAssured.given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(sectionRequest)
                    .when()
                    .log().all()
                    .post(format("/%d/sections", lineId))
                    .then().extract();

            구간_생성_헤더_검증(response);

            SectionResponse sectionResponse = response.as(SectionResponse.class);

            구간_생성_본문_검증(sectionResponse, expectId, sectionRequest);
        };
    }

    private void 구간_생성_본문_검증(SectionResponse sectionResponse, Long expectId, SectionRequest sectionRequest) {
        assertThat(sectionResponse.getId())
                .isEqualTo(expectId);
        assertThat(sectionResponse.getUpStationId())
                .isEqualTo(sectionRequest.getUpStationId());
        assertThat(sectionResponse.getDownStationId())
                .isEqualTo(sectionRequest.getDownStationId());
        assertThat(sectionResponse.getDistance())
                .isEqualTo(sectionRequest.getDistance());
    }

    private void 구간_생성_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE))
                .isIn(ContentType.JSON.getContentTypeStrings());
    }

    private void 정상_응답_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE)).isIn(ContentType.JSON.getContentTypeStrings());
    }
}
