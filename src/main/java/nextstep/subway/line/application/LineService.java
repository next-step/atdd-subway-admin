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
    private static final String LINE_NOT_FOUND_MESSAGE = "원하시는 지하천 노선을 찾지 못했습니다.";

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> selectAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(LineResponse::of).collect(Collectors.toList());
    }

    public LineResponse selectLine(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE)));
    }

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND_MESSAGE));
        line.update(request.toLine());
        return LineResponse.of(lineRepository.save(line));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
