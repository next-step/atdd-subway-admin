package nextstep.subway.line.application;

import nextstep.subway.line.constants.MessageConstants;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        return LineResponse.of(lineRepository.save(request.toLine()));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException(MessageConstants.NO_SUCH_LINE + id)));
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException(MessageConstants.NO_SUCH_LINE + id));
        line.update(request.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
