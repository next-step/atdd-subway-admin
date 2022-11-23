package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.utils.CommonMethodFixture;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class LineAcceptanceStep extends CommonMethodFixture {
    public static final String SLASH = "/";
    public static final String LINE_PATH = "/lines";

    public static ExtractableResponse<Response> 노선_한개_생성한다(Long upLastStationId, Long downLastStationId) {
        LineRequest request = LineRequest.builder()
                .name("2호선")
                .color("green darken-2")
                .distance(7)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();

        return 노선을_생성한다(request);
    }


    public static void 지하철_노선_정보_수정_확인(int id, ExtractableResponse<Response> result) {
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> savedLine = 특정_노선을_조회한다(id);

        assertAll(
                ()-> assertThat(savedLine.jsonPath().getString("name")).isEqualTo("새로운 노선"),
                ()-> assertThat(savedLine.jsonPath().getString("color")).isEqualTo("파란색")
        );
    }

    public static ExtractableResponse<Response> 노선_정보를_수정한다(int id) {
        LineRequest updateRequest = LineRequest.builder()
                .name("새로운 노선")
                .color("파란색")
                .distance(33)
                .build();

        String path = LINE_PATH + SLASH + id;
        return put(path, updateRequest);
    }

    public static ExtractableResponse<Response> 노선을_생성한다(LineRequest request) {
        String path = LINE_PATH + SLASH;
        return post(path, request);
    }

    public static List<String> 모든_노선_이름을_조회한다() {
        String path = LINE_PATH + SLASH;
        return get(path).jsonPath().getList("name", String.class);
    }

    public static void 노선_이름이_조회된다(List<String> allLineNames, String lineName) {
        assertThat(allLineNames).containsAnyOf(lineName);
    }

    public static void 노선_2개_생성(Long upLastStationId, Long downLastStationId) {
        LineRequest request1 = LineRequest.builder()
                .name("2호선")
                .color("red darken-2")
                .distance(100)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request1);

        LineRequest request2 = LineRequest.builder()
                .name("분당선")
                .color("yellow darken-1")
                .distance(20)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();
        노선을_생성한다(request2);
    }


    public static ExtractableResponse<Response> 모든_노선을_조회한다() {
        String path = LINE_PATH + SLASH;
        return get(path);
    }

    public static void 노선의_수가_일치한다(ExtractableResponse<Response> allLines, int size) {
        assertThat(allLines.jsonPath().getList("name", String.class)).hasSize(size);
    }

    public static ExtractableResponse<Response> 특정_노선을_조회한다(int id) {
        String path = LINE_PATH + SLASH + id;
        return get(path);
    }

    public static void 지하철_노선_정보_확인(ExtractableResponse<Response> savedLine, ExtractableResponse<Response> result) {
        assertAll(
                () -> assertThat(result.jsonPath().getInt("id")).isEqualTo(savedLine.jsonPath().getInt("id")),
                () -> assertThat(result.jsonPath().getString("name")).isEqualTo(savedLine.jsonPath().getString("name")),
                ()-> assertThat(result.jsonPath().getString("color")).isEqualTo(savedLine.jsonPath().getString("color"))
        );
    }

    public static ExtractableResponse<Response> 특정_노선을_제거한다(int id) {
        String path = LINE_PATH + SLASH + id;
        return delete(path);
    }

    public static void 해당_노선의_정보가_삭제된다(int id) {
        ExtractableResponse<Response> response = 특정_노선을_조회한다(id);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public static void 상행_구간_삭졔_확인(int lineId) {
        LineResponse savedLine = 특정_노선을_조회한다(lineId).as(LineResponse.class);

        List<String> allNames = savedLine.getStations().stream()
                .map(s -> s.getName()).collect(Collectors.toList());

        assertAll(
                () -> assertThat(allNames).hasSize(2),
                () -> assertThat(allNames).containsExactly("역삼역" ,"선릉역"));

    }

    public static void 하행_구간_삭졔_확인(int lineId) {
        LineResponse savedLine = 특정_노선을_조회한다(lineId).as(LineResponse.class);

        List<String> allNames = savedLine.getStations().stream()
                .map(s -> s.getName()).collect(Collectors.toList());

        assertAll(
                () -> assertThat(allNames).hasSize(2),
                () -> assertThat(allNames).containsExactly("강남역" ,"역삼역"));

    }

    public static void 중간_구간_삭졔_확인(int lineId) {
        LineResponse savedLine = 특정_노선을_조회한다(lineId).as(LineResponse.class);

        List<String> allNames = savedLine.getStations().stream()
                .map(s -> s.getName()).collect(Collectors.toList());

        assertAll(
                () -> assertThat(allNames).hasSize(2),
                () -> assertThat(allNames).containsExactly("강남역" ,"선릉역"),
                () -> assertThat(savedLine.getTotalDistance()).isEqualTo(7));

    }

}
