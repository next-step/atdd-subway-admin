package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
              .map(LineResponse::of)
              .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Optional<Line> line = findById(id);
        return line.map(LineResponse::of).orElseGet(LineResponse::fail);
    }

    public LineResponse saveLine(LineRequest request) {
        Optional<Line> maybeLine = findByName(request.toLine());
        if (maybeLine.isPresent()) {
            throw new IllegalArgumentException("[name="+ request.getName() + "] 이미 등록된 노선입니다.");
        }

        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Optional<Line> maybeLine = findById(id);
        if (!maybeLine.isPresent()) {
            throw new IllegalArgumentException("[id="+ id + "] 노선정보가 존재하지 않습니다.");
        }

        maybeLine.get().update(lineRequest.toLine());
    }

    private Optional<Line> findById(Long id) {
        return lineRepository.findById(id);
    }

    private Optional<Line> findByName(Line line) {
        return lineRepository.findByName(line.getName());
    }
}
