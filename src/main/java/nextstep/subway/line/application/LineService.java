package nextstep.subway.line.application;

import java.util.Optional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
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
        Line line = request.toLine();
        validateDuplicateLine(line);

        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private void validateDuplicateLine(Line line) {
        Optional<Line> optionalLine = Optional.ofNullable(
            lineRepository.findByName(line.getName()));
        optionalLine.ifPresent(findLine -> {
            throw new RuntimeException("이미 등록된 노선 이름을 사용할 수 없습니다.");
        });
    }
}
