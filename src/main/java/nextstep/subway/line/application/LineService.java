package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse save(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
    
    public LineResponse find(Long id) {
        return LineResponse.of(findById(id));
    }
    
    public void update(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.toLine());
    }
    
    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("노선이 없습니다."));
    }
}
