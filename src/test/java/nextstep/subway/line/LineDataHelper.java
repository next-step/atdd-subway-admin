package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
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
}
