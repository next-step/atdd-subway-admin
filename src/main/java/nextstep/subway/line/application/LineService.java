package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    public static final String COULD_NOT_FIND_LINE = "Could not find line ";

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_LINE + id));
        return LineResponse.of(findLine);
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line updateLine = lineRepository.findById(id)
                .map(line -> {
                    line.update(request.toLine());
                    return lineRepository.save(line);
                })
                .orElseThrow(() -> new EntityNotFoundException(COULD_NOT_FIND_LINE + id));
        return LineResponse.of(updateLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

}
