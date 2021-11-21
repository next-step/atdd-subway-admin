package nextstep.subway.line.ui;

import nextstep.subway.line.dto.LineRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class LineRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LineRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LineRequest lineRequest = (LineRequest) target;
        if (!lineRequest.hasSectionArguments()) {
            return;
        }

        if (lineRequest.getUpStationId() == null) {
            errors.rejectValue("upStationId", "EMPTY", "upStationId을 입력하세요.");
        }
        if (lineRequest.getDownStationId() == null) {
            errors.rejectValue("downStationId", "EMPTY", "downStationId을 입력하세요.");
        }
        if (lineRequest.getDistance() == 0) {
            errors.rejectValue("distance", "EMPTY", "distance을 입력하세요");
        }
    }
}
