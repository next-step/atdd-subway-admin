package nextstep.subway.line.application;

import nextstep.subway.exception.LineExistsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
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

    public LineResponse saveLine(LineRequest request) {

        lineRepository.findByName(request.getName())
                .ifPresent(line -> {
                    throw new LineExistsException();
                });
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of).collect(Collectors.toList());
    }


    public LineResponse getLine(Long lineId) {
        return LineResponse.of(findById(lineId));
    }

    public Line findById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));
    }

    public LineResponse updateLine(Long lineId, Line line) {
        Line savedLine = findById(lineId);
        savedLine.update(line);
        return LineResponse.of(lineRepository.save(savedLine));
    }

    public void deleteLineById(Long lineId) {
        Line savedLine = findById(lineId);
        lineRepository.delete(savedLine);
    }

}
