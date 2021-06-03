package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllStations() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long lineId) {
        Line id = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));
        return LineResponse.of(id);
    }

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LineResponse updateById(final Long lineId,final LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
