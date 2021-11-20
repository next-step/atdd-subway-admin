package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
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
    public static final String LINE_NOT_FOUND_MESSAGE = "노선이 없습니다.";
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    private Line getLine(long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE));
    }

    public LineResponse updateLine(LineRequest lineRequest, long id) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }
}
