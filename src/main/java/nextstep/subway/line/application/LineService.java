package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        checkLine(line);
        return LineResponse.of(line.get());
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Optional<Line> line = lineRepository.findById(id);
        checkLine(line);

        line.get().update(lineRequest.toLine());

        return LineResponse.of(line.get());
    }

    private void checkLine(Optional<Line> line) {
        if(!line.isPresent()) {
            throw new RuntimeException("노선을 찾을 수 없습니다.");
        }
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
