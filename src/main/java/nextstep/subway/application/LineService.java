package nextstep.subway.application;

import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        return null;
    }

    public List<LineResponse> findAllLines() {
        return null;
    }

    public LineResponse findById(long lineId) {
        return null;
    }

    public void updateLine(long lineId, LineUpdateRequest lineUpdateRequest) {
    }

    public void removeById(long lineId) {
    }
}
