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
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> fineAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        line.update(new Line(lineRequest.getName(), lineRequest.getColor()));
        return LineResponse.of(line);
    }

    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
