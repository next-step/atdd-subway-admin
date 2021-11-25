package nextstep.subway.line.application;

import javassist.NotFoundException;
import nextstep.subway.exceptions.DuplicateLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateLineException("중복된 지하철 노선 이름이 있습니다.");
        }
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
          .stream()
          .map(LineResponse::of)
          .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("노선을 찾을 수 없습니다."));
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("노선을 찾을 수 없습니다."));
        line.update(lineRequest.toLine());
    }
}
