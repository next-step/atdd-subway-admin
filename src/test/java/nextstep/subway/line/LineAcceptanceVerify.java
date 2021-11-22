package nextstep.subway.line;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LineAcceptanceVerify {

    public static void 지하철_노선_수정_시간_검증(ExtractableResponse<Response> response, LineResponse lineResponse) {
        final String modifiedDate = response.jsonPath().getString("modifiedDate");
        assertTrue(LocalDateTime.parse(modifiedDate).isAfter(lineResponse.getModifiedDate()));
    }

    public static void 지하철_노선_응답_항목_검증(ExtractableResponse<Response> response, LineResponse createdLineResponse) {
        final JsonPath responseJson = response.jsonPath();
        assertThat(responseJson.getString("name")).isEqualTo(createdLineResponse.getName());
        assertThat(responseJson.getString("color")).isEqualTo(createdLineResponse.getColor());
        assertThat(responseJson.getLong("id")).isEqualTo(createdLineResponse.getId());
//        assertThat(responseJson.getList("sectionResponses", SectionResponse.class)).containsAll(createdLineResponse.getSectionResponses());
//        assertThat(responseJson.getList("sectionResponses", SectionResponse.class).get(0)).isEqualTo(createdLineResponse.getSectionResponses().get(0));
//        assertThat(responseJson.getList("sectionResponses", SectionResponse.class).get(1)).isEqualTo(createdLineResponse.getSectionResponses().get(1));


    }

    public static void 지하철_노선_생성_결과_검증(ExtractableResponse<Response> response) {
        final Long createdId = response.jsonPath().getLong("id");
        assertAll(() -> {
            assertThat(createdId).isGreaterThan(0L);
        });
    }

    public static void 지하철_노선_목록의_항목_검증(ExtractableResponse<Response> response, List<LineResponse> lines) {
        final List<String> names = lines.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        final List<String> colors = lines.stream()
                .map(LineResponse::getColor)
                .collect(Collectors.toList());

        final JsonPath responseJsonPath = response.jsonPath();
        assertThat(responseJsonPath.getList("name", String.class)).containsAll(names);
        assertThat(responseJsonPath.getList("color", String.class)).containsAll(colors);
    }


    public static void 지하철_노선_목록_포함_검증(ExtractableResponse<Response> response, List<LineResponse> lines) {
        final List<LineResponse> lineListResponse = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lineListResponse).containsAll(lines);
    }
}
