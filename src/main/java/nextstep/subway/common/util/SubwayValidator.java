package nextstep.subway.common.util;

import nextstep.subway.common.message.ExceptionMessage;

public class SubwayValidator {
    private SubwayValidator() {
    }

    public static void validateNotNullAndNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

}
