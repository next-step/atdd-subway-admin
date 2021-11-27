package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LinesResponse getLines() {
        List<Line> lines = lineRepository.findAll();
        return new LinesResponse(lines);
    }

    public LineResponse getLine(Long id) throws NotFoundException {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(NotFoundException::new);
    }

    public LineResponse updateLine(Long id, LineRequest request) throws NotFoundException {
        return lineRepository.findById(id)
                .map(line -> line.update(request.toLine()))
                .map(LineResponse::of)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
