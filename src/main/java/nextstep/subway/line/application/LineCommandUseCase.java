package nextstep.subway.line.application;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public interface LineCommandUseCase {
    LineResponse saveLine(LineRequest lineRequest);

    void updateLine(Long id, LineRequest lineRequest);

    void deleteLine(Long id);
}
