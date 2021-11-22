package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.DuplicateException;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        validateDuplicateLine(line);

        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = findLine(id);

        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findByLineId(Long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElse(null);
    }

    public void deleteLineById(Long id) {
        Line line = findLine(id);
        line.delete();

        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    protected Line findLine(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected void validateDuplicateLine(Line line) {
        Optional<Line> optionalLine = lineRepository.findByName(line.getName());

        optionalLine.ifPresent(findLine -> {
            throw new DuplicateException("이미 등록된 노선 이름을 사용할 수 없습니다.");
        });
    }
}
