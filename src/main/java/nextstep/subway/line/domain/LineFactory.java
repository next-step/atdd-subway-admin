package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineRequest;

public interface LineFactory {

    Line create(final LineRequest lineRequest);
}
