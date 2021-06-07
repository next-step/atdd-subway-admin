package nextstep.subway.line.application;

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
        validateDuplicate(request);

        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void validateDuplicate(LineRequest request) {
        if (this.findByName(request.getName()).size() != 0) {
            throw new IllegalArgumentException("노선이름이 이미 존재합니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public List<LineResponse> findByName(String name) {
        List<Line> lines = lineRepository.findByName(name);

        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }
}
