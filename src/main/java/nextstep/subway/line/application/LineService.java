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

    private List<Line> findByName(String name) {
        return lineRepository.findByName(name);
    }

    public List<LineResponse> updateLine(LineRequest request) {
        Line line = request.toLine();
        List<Line> lines = this.findByName(line.getName());

        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).update(line);
        }

        List<Line> persistLines = lineRepository.saveAll(lines);
        return persistLines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public List<LineResponse> getLine(String name) {
        return this.findByName(name).stream().map(LineResponse::of).collect(Collectors.toList());
    }
}
