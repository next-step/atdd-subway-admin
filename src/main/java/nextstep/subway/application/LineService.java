package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        Lines lines = new Lines(lineRepository.findAll());

        return lines.asList().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }
}
