package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;

public class SectionAcceptanceMethod {
    private static final String SECTIONS_URI = "/sections";

    public static ExtractableResponse<Response> 지하철_구간_추가(ExtractableResponse<Response> 지하철노선_생성_응답, SectionRequest 지하철구간_요청) {
        return null;
    }

    public static void 추가된_지하철구간_확인(ExtractableResponse<Response> 구간_추가_응답) {
    }

    public static void 추가된_지하철구간_길이_조회(SectionRequest 지하철구간_요청) {
    }

    public static void 새로운역_추가_안됨(ExtractableResponse<Response> 구간_추가_응답) {
    }
}
