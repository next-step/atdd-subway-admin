package nextstep.subway.line.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private final LineService lineService;

    public SectionService(LineService lineService) {
        this.lineService = lineService;
    }

    public LineResponse saveSection(Long lineId, SectionRequest request) {
        Line line = lineService.getLineById(lineId);
        Line persistLine = lineService.addSection(line, request);
        return LineResponse.of(persistLine);
    }
}
