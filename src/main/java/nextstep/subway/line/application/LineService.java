package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineCreateRequest request){
        return null;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(Long id) {
        return LineResponse.of(line(id));
    }

    public void update(Long id, LineUpdateRequest request) {
        Line line = line(id);
        line.update(request.toName(), request.toColor());
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    private Line line(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("line id(%d) does not exist", id)));
    }
}
