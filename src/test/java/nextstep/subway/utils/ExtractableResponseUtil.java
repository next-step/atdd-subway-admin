package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractableResponseUtil {

    public static Long 응답에서_ID_추출(ExtractableResponse response) {
        return Long.parseLong(response.header(HttpHeaders.LOCATION).split("/")[2]);
    }

    public static List<Long> 여러_응답에서_ID_추출(ExtractableResponse... responses) {
        return Arrays.asList(responses).stream()
                .map(ExtractableResponseUtil::응답에서_ID_추출)
                .collect(Collectors.toList());
    }
}
