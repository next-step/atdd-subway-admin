package nextstep.subway.line.application;

import nextstep.subway.common.exception.ElementNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void updateLine(Long id, LineRequest request) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        if (!optionalLine.isPresent()) {
            throw new ElementNotFoundException();
        }
        Line line = optionalLine.get();
        line.update(request.toLine());
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        if (!optionalLine.isPresent()) {
            throw new ElementNotFoundException();
        }

        return LineResponse.of(optionalLine.get());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
