package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public final class HttpTestStatusCode {
    public static void 컨텐츠_생성됨(ExtractableResponse<Response> response) {
        응답상태코드확인(response, HttpStatus.CREATED);
    }

    public static void 서버_내부_에러(ExtractableResponse<Response> response) {
        응답상태코드확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void 컨텐츠_없음(ExtractableResponse<Response> response) {
        응답상태코드확인(response, HttpStatus.NO_CONTENT);
    }

    public static void 요청_완료(ExtractableResponse<Response> response) {
        응답상태코드확인(response, HttpStatus.OK);
    }

    private static void 응답상태코드확인(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }
}
