package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        if (optionalLine.isPresent()) {
            return LineResponse.of(optionalLine.get());
        }
        throw new IllegalArgumentException("존재하지 않는 노선입니다.");
    }
}
