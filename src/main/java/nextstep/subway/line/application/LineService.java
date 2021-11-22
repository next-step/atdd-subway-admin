package nextstep.subway.line.application;

import nextstep.subway.line.application.dto.LineUpdateRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() ->
                        new EntityNotFoundException("노선이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    public LineResponse update(LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(lineUpdateRequest.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("노선이 존재하지 않습니다."));
        line.update(lineUpdateRequest);

        return LineResponse.of(line);
    }

    public void delete(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
