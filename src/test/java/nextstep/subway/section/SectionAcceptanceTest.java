package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
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

import static nextstep.subway.line.LineAcceptanceTest.라인_생성_및_체크;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("구간 테스트")
class SectionAcceptanceTest extends AcceptanceTest {
    private static Long 강남역_ID = 1L;
    private static Long 역삼역_ID = 2L;
    private static Long 수진역_ID = 3L;

    private static SectionRequest 강남역_역삼역_길이_15 = new SectionRequest(강남역_ID, 역삼역_ID, 15L);

    @TestFactory
    @DisplayName("신규 구간을 추가한다")
    Stream<DynamicTest> 신규_구간을_추가한다() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(LineAcceptanceTest.분당_라인, 1L, new StationRequest[] {강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(강남역_역삼역_길이_15, 2L))
        );
    }

    private Executable 구간_생성_및_체크(SectionRequest sectionRequest, Long expectId) {
        return () -> {
            ExtractableResponse<Response> response = RestAssured.given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(sectionRequest)
                    .when()
                    .log().all()
                    .post("/1/sections")
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
                .isEqualTo(HttpStatus.OK.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE))
                .isIn(ContentType.JSON.getContentTypeStrings());
    }
}
