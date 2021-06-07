package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

        Line persistLine = lineRepository.save(request.toLine()) ;
        return LineResponse.of(persistLine);
    }

    private void validateDuplicate(LineRequest request) {
        Line line = request.toLine();

        if (Objects.nonNull(lineRepository.findByName(line.getName()))) {
            throw new IllegalArgumentException("노선이름이 이미 존재합니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    private Line findByName(String name) {
        Line line = lineRepository.findByName(name);

        return Optional.ofNullable(line).orElseThrow(() -> new NullPointerException("존재하지 않는 노선입니다."));
    }

    public LineResponse updateLine(LineRequest request) {
        Line line = request.toLine();
        Line persistLine = this.findByName(line.getName());

        persistLine.update(line);

        return LineResponse.of(lineRepository.save(persistLine));
    }

    public LineResponse getLine(String name) {
        return LineResponse.of(this.findByName(name));
    }

    public LineResponse deleteLine(LineRequest request) {
        Line line = request.toLine();
        Line persistLine = this.findByName(line.getName());

        lineRepository.delete(persistLine);

        return LineResponse.of(persistLine);
    }
}
