package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineNotFoundException;
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

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        validateLine(request);
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest updateLineRequest) {
        Line line = findById(id);
        line.update(updateLineRequest.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private void validateLine(LineRequest request) {
        if (isNameDuplicated(request)) {
            throw new LineNameDuplicatedException();
        }
    }

    private boolean isNameDuplicated(LineRequest request) {
        return lineRepository.existsByName(request.getName());
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }
}
