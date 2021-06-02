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
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 LINE 입니다."));

        return LineResponse.of(line);
    }

    public LineResponse updateLine(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).get();
        line.setName(lineRequest.getName());
        line.setColor(lineRequest.getColor());
        return LineResponse.of(line);
    }

    public void deleteLine(Long lineId) {
        lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 LINE 입니다."));
        lineRepository.deleteById(lineId);
    }
}
