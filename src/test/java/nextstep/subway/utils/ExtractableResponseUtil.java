package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractableResponseUtil {

    public static Long extractIdInResponse(ExtractableResponse response) {
        return Long.parseLong(response.header(HttpHeaders.LOCATION).split("/")[2]);
    }

    public static List<Long> extractIdInResponses(ExtractableResponse... responses) {
        return Arrays.asList(responses).stream()
                .map(ExtractableResponseUtil::extractIdInResponse)
                .collect(Collectors.toList());
    }
}
