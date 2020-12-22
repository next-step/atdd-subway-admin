package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;
import java.util.stream.Collectors;

public class LocationUtil {
    public static List<Long> getIdsToLocationHeaders(List<ExtractableResponse<Response>> createResponse) {
        return createResponse.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }
}
