package nextstep.subway.line.application;

import javax.persistence.EntityNotFoundException;
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

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineBy(Long id) {
        Line line = lineRepository
            .findById(id)
            .orElseThrow(EntityNotFoundException::new);

        return LineResponse.of(line);
    }

    public LineResponse updateLineBy(Long id, LineRequest lineRequest) {
        Line line = lineRepository
            .findById(id)
            .orElseThrow(EntityNotFoundException::new)
            .getUpdatedLineBy(lineRequest);

        return LineResponse.of(line);
    }

    public void deleteStationBy(Long id) {
        lineRepository.deleteById(id);
    }
}
