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

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findByLineId(Long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElse(null);
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line persistLine = findLine(id);

        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        Line line = findLine(id);
        line.delete();

        lineRepository.save(line);
    }

    private Line findLine(Long id) {
        Optional<Line> optionalPersistLine = lineRepository.findById(id);
        if (!optionalPersistLine.isPresent()) {
            throw new NotFoundException();
        }

        return optionalPersistLine.get();
    }

    private void validateDuplicateLine(Line line) {
        Optional<Line> optionalLine = Optional.ofNullable(
            lineRepository.findByName(line.getName()));

        optionalLine.ifPresent(findLine -> {
            throw new DuplicateException("이미 등록된 노선 이름을 사용할 수 없습니다.");
        });
    }
}
