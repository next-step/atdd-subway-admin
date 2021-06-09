package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public interface LineQueryUseCase {
    List<LineResponse> findAllLines();

    LineResponse findLine(Long id);

    Line findById(Long id);

    void checkDuplicatedLineName(String name);
}
