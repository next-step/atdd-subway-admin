package nextstep.subway.line.application;

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
        // 이름, 컬러, 상향역id, 하향역id, 역간격 입력받는다.
        // 상향역 있는지 확인해서 Station 찾아둔다.
        // 하향역 있는지 확인해서 Station 찾아둔다.
        // Line 에서 Section 만들 수 있는지 확인한다.
        // Section 만들어서 Line의 제 위치에 넣는다.
        // Line 저장한다.
        // 저장한 Line 반환한다.
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.listOf(lines);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        // Line 찾는다.
        // Line 내에 있는 모든 Section 가져온다.
        // 모든 Section 에 있는 모든 Station 가져온다. (상행~ 하행)
        // LineResponse 만든다.
        // LineResponse 반환한다.
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        persistLine.update(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public Long deleteLineById(Long id) {
        lineRepository.deleteById(id);
        return id;
    }
}
