package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public LineResponse findLine(Long lineId) {
        Line line = findById(lineId);
        return LineResponse.of(line);
    }

    public List<LineResponse> findLines() {
        List<Line> lineList = lineRepository.findAll();
        return lineList
                .stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public Long updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);

        line.update(lineRequest.toLine());
        Line save = lineRepository.save(line);
        return save.getId();
    }

    public void deleteLine(Long lineId) {
        Line line = findById(lineId);

        lineRepository.delete(line);
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NotFoundException());
    }

}
