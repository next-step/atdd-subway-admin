package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.function.NvlFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.lang.String.format;

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

    public LineResponse getLineById(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        return LineResponse.of(line.orElseThrow(() -> new NullPointerException(format("id가 %d인 노선이 존재 하지 않습니다.", id))));
    }

    public LinesResponse getLines(LineRequest lineRequest) {
        return LinesResponse.of(lineRepository.findByNameContainingAndColorContaining(lineRequest.getName(), lineRequest.getColor()));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        // 1.없는 경우, 2.유효하지 않은 요청
        Optional<Line> line = lineRepository.findById(id);
        Line updatingLine = line.orElseThrow(() -> new NullPointerException(format("id가 %d인 노선이 존재 하지 않습니다.", id)));
        lineRequest.changeValues(updatingLine);

        return LineResponse.of(lineRepository.save(updatingLine));
    }
}
