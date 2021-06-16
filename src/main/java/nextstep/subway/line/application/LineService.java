package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static java.lang.String.format;

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

    public LineResponse getLineById(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        return LineResponse.of(line.orElseThrow(() -> new EntityNotFoundException(format("id가 %d인 노선이 존재 하지 않습니다.", id))));
    }

    public LinesResponse getLines(LineRequest lineRequest) {
        return LinesResponse.of(lineRepository.findByNameContainingAndColorContaining(lineRequest.getName(), lineRequest.getColor()));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Optional<Line> line = lineRepository.findById(id);
        Line updatingLine = line.orElseThrow(() -> new EntityNotFoundException((format("id가 %d인 노선이 존재 하지 않습니다.", id))));
        updatingLine.update(lineRequest.toLine());

        return LineResponse.of(lineRepository.save(updatingLine));
    }

    public void deleteLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        Line deletingLine = line.orElseThrow(() -> new EntityNotFoundException((format("id가 %d인 노선이 존재 하지 않습니다.", id))));
        lineRepository.delete(deletingLine);
    }
}
