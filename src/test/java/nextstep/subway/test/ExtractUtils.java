package nextstep.subway.test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

public class ExtractUtils {

    private static final String ID= "id";
    private static final String NAME= "name";

    public static long extractId(ExtractableResponse<Response> response){
        return response.body().jsonPath().getLong(ID);
    }

    public static String extractName(ExtractableResponse<Response> response) {
        return response.body().jsonPath().getString(NAME);
    }

    public static List<String> extractNames(ExtractableResponse<Response> response){
        return response.body().jsonPath().getList(NAME);
    }

    public static <T> T extract(String elementPath,ExtractableResponse<Response> response){
        return response.body().jsonPath().get(elementPath);
    }

    public static <T> List<T> extract(String elementPath,ExtractableResponse<Response> response,Class<T> clazz){
        return response.body().jsonPath().getList(elementPath,clazz);
    }
}
