package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineCreateResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineCreateResponse.of(persistLine);
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findOne(Long id) {
        return LineResponse.of(findLine(id));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException("라인을 찾을 수 없습니다."));
    }

    public void deleteLind(Long id) {
        lineRepository.deleteById(id);
    }
}
