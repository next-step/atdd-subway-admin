package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final EntityManager entityManager;

    public LineService(LineRepository lineRepository, EntityManager entityManager) {
        this.lineRepository = lineRepository;
        this.entityManager = entityManager;
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철노선 ID 입니다."));

        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toLine(entityManager));
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse updateLineById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철노선 ID 입니다."));

        line.update(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
