package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.Asserts;
import nextstep.subway.utils.Methods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성")
    @Nested
    class CreateLineTest {
        private StationResponse 강남역;
        private StationResponse 광교역;

        @BeforeEach
        void setUp() {
            강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
        }

        @DisplayName("지하철 노선을 생성한다.")
        @Test
        void createLine() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선이_생성된다(response);
        }

        @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
        @Test
        void givenDuplicateLineNameThenFail() {
            // given
            지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선명으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyNameThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선색상으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyColorThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("황금노선현", "");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("상행선, 하행선, 거리가 포함된 노선을 생성 요청한다")
        @Test
        void givenSectionWhenCreateThenOk() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "10");

            // then
            지하철_노선이_생성된다(response);
        }

        @DisplayName("상행선, 하행선, 거리 중 한개 이상 미포함하여 노선을 생성 요청한다")
        @Test
        void givenInvalidSectionWhenCreateThenReturnFail() {
            // given
            // 상행선, 하행선, 거리가 각각 하나씩 없는 조건 배열
            String[][] conditions = {
                    {강남역.getId().toString(), "", "11"},
                    {"", 광교역.getId().toString(), "11"},
                    {강남역.getId().toString(), 광교역.getId().toString(), ""}};

            for (String[] arg : conditions) {
                // when
                ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "blue", arg[0], arg[1], arg[2]);

                // then
                지하철_노선_생성이_실패한다(response);
            }
        }

        @DisplayName("상행선, 하행선에 등록이 안된 역을 포함한 노선을 생성 요청한다")
        @Test
        void givenHasNotStationWhenCreateThenReturnFail() {
            // given
            // 상행선, 하행선 각 각 없는 역 ID를 담은 배열
            String[][] conditions = {
                    {강남역.getId().toString(), "999999"},
                    {"999999", 광교역.getId().toString()}};

            for (String[] arg : conditions) {
                // when
                ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "blue", arg[0], arg[1], "20");

                // then
                등록된_역_없음_사유로_지하철_노선_생성이_실패한다(response);
            }
        }
    }

    @DisplayName("지하철 노선 목록 조회")
    @Nested
    class GetLinesTest {
        @DisplayName("지하철 노선 목록을 조회한다.")
        @Test
        void getLines() {
            // given
            ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음("박달-강남선", "blue");
            ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음("광명-구디선", "green");

            // when
            ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

            // then
            지하철_노선_목록이_응답된다(response);
            지하철_노선_목록이_포함된다(createResponse1, createResponse2, response);
        }

        @DisplayName("역이 포함된 지하철 노선을 조회한다.")
        @Test
        void getLineWithSection() {
            // given
            StationResponse 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            StationResponse 광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
            ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "10");
            ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음("신분당선2", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "10");

            // when
            ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

            // then
            지하철_노선_목록이_응답된다(response);
            지하철_노선_목록이_포함된다(createResponse1, createResponse2, response);
        }
    }

    @DisplayName("지하철 노선 조회")
    @Nested
    class GetLineTest {
        @DisplayName("지하철 노선을 조회한다.")
        @Test
        void getLine() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

            // then
            지하철_노선을_응답한다(response);
        }

        @DisplayName("등록되지 않은 지하철 노선을 조회한다.")
        @Test
        void givenHasNotIdThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

            // then
            지하철_노선_응답이_실패한다(response);
        }

        @DisplayName("상행선 하행선 순으로 정렬된 역들이 포함된 지하철 노선을 조회한다.")
        @Test
        void getLineWithSection() {
            // given
            StationResponse 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            StationResponse 광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "10");

            // when
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

            // then
            지하철_노선을_응답한다(response);
            상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(response, 강남역, 광교역);
        }
    }

    @DisplayName("지하철 노선 수정")
    @Nested
    class ModifyLineTest {
        @DisplayName("지하철 노선을 수정한다.")
        @Test
        void updateLine() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");
            Map<String, String> toBeParams = createLineParams("강남-박달선", "red");

            // when
            ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, toBeParams);

            // then
            지하철_노선이_수정된다(response);
            지하철_노선이_수정된_이름_색상으로_변경된다(createResponse, toBeParams);
        }

        @DisplayName("등록되지 않은 지하철 노선을 수정한다.")
        @Test
        void givenHasNotIdThenFail() {
            // given
            Map<String, String> toBeParams = createLineParams("강남-박달선", "red");

            // when
            ExtractableResponse<Response> response = 지하철_노선_수정_요청(1L, toBeParams);

            // then
            지하철_노선을_찾을_수_없어_실패한다(response);
        }

        @DisplayName("공백의 노선명으로 지하철 노선을 수정한다.")
        @Test
        void givenEmptyNameThenFail() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");
            Map<String, String> toBeParams = createLineParams("", "red");

            // when
            ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, toBeParams);

            // then
            지하철_노선_수정에_실패한다(response);
        }

        @DisplayName("공백의 노선색상으로 지하철 노선을 수정한다.")
        @Test
        void givenEmptyColorThenFail() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");
            Map<String, String> toBeParams = createLineParams("강남-박달선", "");

            // when
            ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, toBeParams);

            // then
            지하철_노선_수정에_실패한다(response);
        }
    }

    @DisplayName("지하철 노선 삭제")
    @Nested
    class DeleteLineTest {
        @DisplayName("지하철 노선을 제거한다.")
        @Test
        void deleteLine() {
            // given
            ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse);

            // then
            지하철_노선이_삭제된다(createResponse, response);
        }

        @DisplayName("등록되지 않은 지하철 노선을 제거한다.")
        @Test
        void givenHasNotLineThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_삭제_요청(1L);

            // then
            지하철_노선을_찾을_수_없어_실패한다(response);
        }
    }

    @DisplayName("구간 추가 기능")
    @Nested
    class AddSectionTest {
        @DisplayName("상행선이 같은 새로운 구간을 등록한다")
        @Test
        void testInsertionSection() {
            // given
            StationResponse 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            StationResponse 광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
            StationResponse 안양역 = StationAcceptanceTest.지하철역_등록되어_있음("안양역").as(StationResponse.class);
            ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "100");
            String lineUrl = 신분당선.header("Location");

            // when
            ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineUrl, 강남역, 안양역, 40);

            // then
            지하철_구간이_생성된다(response);
            상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(lineUrl, 강남역, 안양역, 광교역);
        }

        @DisplayName("하행선이 같은 새로운 구간을 등록한다")
        @Test
        void testInsertionTailSection() {
            // given
            StationResponse 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            StationResponse 광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
            StationResponse 안양역 = StationAcceptanceTest.지하철역_등록되어_있음("안양역").as(StationResponse.class);
            ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), "100");
            String lineUrl = 신분당선.header("Location");

            // when
            ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineUrl, 안양역, 광교역, 40);

            // then
            지하철_구간이_생성된다(response);
            상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(lineUrl, 강남역, 안양역, 광교역);
        }

        @DisplayName("역 사이에 새로운 역을 등록할 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정")
        @Test
        void testDistance() {
            // given
            Integer totalDistance = 100;
            Integer newDistance = 40;
            StationResponse 강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
            StationResponse 광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
            StationResponse 안양역 = StationAcceptanceTest.지하철역_등록되어_있음("안양역").as(StationResponse.class);
            ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId().toString(), 광교역.getId().toString(), totalDistance.toString());
            String lineUrl = 신분당선.header("Location");

            // when
            ExtractableResponse<Response> response = 지하철_구간_추가_요청(lineUrl, 강남역, 안양역, newDistance);

            // then
            새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정한다(totalDistance, newDistance, lineUrl, response);
        }

        private void 새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정한다(Integer totalDistance, Integer distance, String lineUrl, ExtractableResponse<Response> response) {
            assertThat(response.as(SectionResponse.class).getDistance()).isEqualTo(distance);
            ExtractableResponse<Response> sectionsResponse = Methods.get(lineUrl + "/sections");
            List<SectionResponse> list = sectionsResponse.jsonPath().getList(".", SectionResponse.class);
            Integer resultTotalDistance = list.stream().map(SectionResponse::getDistance).reduce(Integer::sum).get();
            assertThat(resultTotalDistance).isEqualTo(totalDistance);
        }

        private void 지하철_구간이_생성된다(ExtractableResponse<Response> response) {
            Asserts.assertIsCreated(response);
        }

        private ExtractableResponse<Response> 지하철_구간_추가_요청(String lineUrl, StationResponse upStation, StationResponse downStation, Integer distance) {
            Map<String, Object> params = new HashMap<>();
            params.put("upStationId", upStation.getId());
            params.put("downStationId", downStation.getId());
            params.put("distance", distance);
            return RestAssured
                    .given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post(lineUrl + "/sections")
                    .then().log().all().extract();
        }
    }

    /**
     * 응답
     */
    private void 지하철_노선이_생성된다(ExtractableResponse<Response> response) {
        Asserts.assertIsCreated(response);
    }

    private void 등록된_역_없음_사유로_지하철_노선_생성이_실패한다(ExtractableResponse<Response> response) {
        Asserts.assertIsNotFound(response);
    }

    private void 지하철_노선_생성이_실패한다(ExtractableResponse<Response> response) {
        Asserts.assertIsBadRequest(response);
    }

    private void 지하철_노선_응답이_실패한다(ExtractableResponse<Response> response) {
        Asserts.assertIsNotFound(response);
    }

    private void 지하철_노선_목록이_응답된다(ExtractableResponse<Response> response) {
        Asserts.assertIsOk(response);
    }

    private void 지하철_노선을_응답한다(ExtractableResponse<Response> response) {
        Asserts.assertIsOk(response);
    }

    private void 지하철_노선이_수정된다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선을_찾을_수_없어_실패한다(ExtractableResponse<Response> response) {
        Asserts.assertIsNotFound(response);
    }

    private void 지하철_노선_수정에_실패한다(ExtractableResponse<Response> response) {
        Asserts.assertIsBadRequest(response);
    }

    /**
     * 검증
     */
    private void 지하철_노선_목록이_포함된다(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", Line.class).stream()
                .map(Line::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(ExtractableResponse<Response> response, StationResponse ... stations) {
        Long[] expectedStationIds = Stream.of(stations).map(StationResponse::getId).toArray(Long[]::new);
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(result.getStations()).hasSize(stations.length)
                .map(StationResponse::getId)
                .containsExactly(expectedStationIds);
    }

    private void 상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(String lineUrl, StationResponse ... stations) {
        ExtractableResponse<Response> result = 지하철_노선_조회_요청(lineUrl);
        상행선_하행선_순으로_정렬된_역을_포함한_지하철_노선을_응답한다(result, stations);
    }

    private void 지하철_노선이_수정된_이름_색상으로_변경된다(ExtractableResponse<Response> createResponse, Map<String, String> toBeParams) {
        ExtractableResponse<Response> modifiedResponse = 지하철_노선_조회_요청(createResponse);
        Line line = modifiedResponse.jsonPath().getObject(".", Line.class);
        assertThat(line.getName()).isEqualTo(toBeParams.get("name"));
        assertThat(line.getColor()).isEqualTo(toBeParams.get("color"));
    }

    private void 지하철_노선이_삭제된다(ExtractableResponse<Response> createResponse, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> deletedResponse = 지하철_노선_조회_요청(createResponse);
        assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * 요청
     */
    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color);
        지하철_노선이_생성된다(response);
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color, String upStationId, String downStationId, String distance) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(name, color, upStationId, downStationId, distance);
        지하철_노선이_생성된다(response);
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        return 지하철_노선_생성_요청(name, color, null, null, null);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, String upStationId, String downStationId, String distance) {
        return RestAssured
                .given().log().all()
                .body(createLineParams(name, color, upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return Methods.get("/lines");
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        return Methods.get(response.header("Location"));
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return Methods.get("/lines/" + id);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String url) {
        return Methods.get(url);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> createResponse, Map<String, String> toBeParams) {
        return Methods.put(createResponse.header("Location"), toBeParams);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Map<String, String> toBeParams) {
        return Methods.put("/lines/" + id, toBeParams);
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(ExtractableResponse<Response> createResponse) {
        return Methods.delete(createResponse.header("Location"));
    }

    private ExtractableResponse<Response> 지하철_노선_삭제_요청(Long id) {
        return Methods.delete("/lines/" + id);
    }

    /**
     * 공통 메소드
     */
    private Map<String, String> createLineParams(String name, String color) {
        return createLineParams(name, color, "", "", "");
    }

    private Map<String, String> createLineParams(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
