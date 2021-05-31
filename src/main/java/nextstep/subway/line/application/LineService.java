package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class LineService {
    private static final String EXIST_LINE = "이미 존재하는 노선입니다 :";
    private static final String LINE_NOT_EXISTED = "노선이 존재하지 않습니다 :";
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Optional<Line> findLine = lineRepository.findByName(request.getName());
        if (findLine.isPresent()) {
            throw new LineDuplicatedException(EXIST_LINE + request.getName());
        }
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(toList());
    }

    public LineResponse getLine(Long lineId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_EXISTED + lineId));
        return LineResponse.of(line);
    }
}
