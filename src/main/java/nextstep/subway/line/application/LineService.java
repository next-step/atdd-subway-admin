package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Repository;
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

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        Line line = findLineById(lineId);
        return LineResponse.of(line);
    }

    public LineResponse update(Long lineId, LineRequest lineRequest) {
        Line lineById = findLineById(lineId);
        lineById.update(lineRequest.toLine());
        return LineResponse.of(lineById);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 LINE 입니다."));
    }

    public Long delete(Long lineId) {
        Line lineById = findLineById(lineId);
        lineRepository.delete(lineById);
        return lineById.getId();
    }
}
