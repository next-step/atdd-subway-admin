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

    public LineResponse editLine(Long id, LineRequest request) {
        return lineRepository.findById(id)
                .map(line -> {
                    line.update(request.toLine());
                    return LineResponse.of(lineRepository.save(line));
                })
                .orElseThrow(IllegalArgumentException::new);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return lineRepository.findById(id)
                .map(LineResponse::of)
                .orElseThrow(IllegalArgumentException::new);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
