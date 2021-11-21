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
        checkDuplicatedName(request.getName());
        
        return LineResponse.of(lineRepository.save(request.toLine()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public LineResponse find(Long id) {
        return LineResponse.of(findById(id));
    }
    
    public void update(Long id, LineRequest request) {
        Line line = findById(id);
        line.update(request.toLine());
    }
    
    public void delete(Long id) {
        Line line = findById(id);
        lineRepository.delete(line);
    }
    
    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "에 해당하는 노선이 없습니다."));
    }
    
    private void checkDuplicatedName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new IllegalArgumentException(String.format("라인 이름(%d)이 중복되었습니다.", name));
        }
    }
}
