package nextstep.subway.common.message;

import java.util.function.BiFunction;

import static java.lang.String.format;

public class DomainExceptionMessage {
    private DomainExceptionMessage() {

    }

    public static final BiFunction<Long, String, String> NOT_FOUND_MESSAGE
            = (id, entityName) -> format("id가 %d인 %s이(가) 존재 하지 않습니다.", id, entityName);
}
