package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LineDataHelper {
    private final LineRepository lineRepository;

    public LineDataHelper(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<Long> 지하철_노선_추가(Line...line) {
        List<Line> lines = lineRepository.saveAll(Arrays.asList(line));
        return lines.stream()
                .map(Line::getId)
                .collect(Collectors.toList());
    }

    public Line 지하철_노선_추가(Line line) {
        return lineRepository.save(line);
    }

    public Optional<Line> 지하철_노선_조회(Long id) {
        return lineRepository.findById(id);
    }

    public List<Long> 속한_지하철_아이디_조회(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        return line.get().getAllIncludedStationIds();
    }
}
