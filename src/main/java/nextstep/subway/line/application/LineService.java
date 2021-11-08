package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
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
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> readLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                    .map(LineResponse::of)
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse readLine(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException(
                                      String.format("id=%s 에 해당하는 노선이 존재하지 않습니다.", id)));
        return LineResponse.of(line);
    }
}
