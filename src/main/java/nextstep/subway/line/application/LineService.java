package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LineResponse saveLine(LineRequest request) {
        validateExists(request.getName());
        final Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void validateExists(String name) {
        if (lineRepository.existsByName(name)) {
            throw new IllegalArgumentException("노선의 이름이 중복되었습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
}
