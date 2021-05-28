package nextstep.subway.line.application;

import java.util.List;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.NotFoundLineException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll()
                             .stream()
                             .map(LineResponse::of)
                             .collect(toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                                  .orElseThrow(() -> new NotFoundLineException(lineId));
        return LineResponse.of(line);
    }

    public void updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId)
                                  .orElseThrow(() -> new NotFoundLineException(lineId));

        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
        lineRepository.save(line);
    }

    public void deleteLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                                  .orElseThrow(() -> new NotFoundLineException(lineId));

        lineRepository.delete(line);
    }
}
