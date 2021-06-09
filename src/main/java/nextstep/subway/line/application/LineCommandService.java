package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService implements LineCommandUseCase {
    private final LineRepository lineRepository;
    private final LineQueryUseCase lineQueryUseCase;

    public LineCommandService(LineRepository lineRepository, LineQueryUseCase lineQueryUseCase) {
        this.lineRepository = lineRepository;
        this.lineQueryUseCase = lineQueryUseCase;
    }

    @Override
    public LineResponse saveLine(LineRequest lineRequest) {
        lineQueryUseCase.checkDuplicatedLineName(lineRequest.getName());
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    @Override
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineQueryUseCase.findById(id);
        if (line.isDifferentName(lineRequest.getName())) {
            lineQueryUseCase.checkDuplicatedLineName(lineRequest.getName());
        }
        line.update(lineRequest.toLine());
    }

    @Override
    public void deleteLine(Long id) {
        Line line = lineQueryUseCase.findById(id);
        lineRepository.delete(line);
    }
}
