package nextstep.subway.line.application;

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
        List<Line> persistLines = lineRepository.findAll();
        return LinesResponse.of(persistLines);
    }

    public LineResponse getLine(Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(persistLine);
    }


    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persisLine = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        persisLine.update(lineRequest.toLine());

        return LineResponse.of(persisLine);
    }
}
