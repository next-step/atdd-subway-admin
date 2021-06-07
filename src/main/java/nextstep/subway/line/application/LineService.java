package nextstep.subway.line.application;

import nextstep.subway.line.application.exception.LineDuplicatedException;
import nextstep.subway.line.application.exception.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public static final String LINE_DUPLICATED_EXCEPTION_MESSAGE = "이미 지하철 노선이 등록되어 있습니다.";
    public static final String LINE_NOT_FOUND_EXCEPTION_MESSAGE = "해당 ID로 된 지하철 노선이 존재하지 않습니다.";
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        checkDuplicatedLine(request.getName());

        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    private void checkDuplicatedLine(String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new LineDuplicatedException(LINE_DUPLICATED_EXCEPTION_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return LineResponse.of(getLine(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        line.update(lineRequest.toLine());
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(LINE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void deleteLine(Long id){
        Line line = getLine(id);
        lineRepository.delete(line);
    }
}
