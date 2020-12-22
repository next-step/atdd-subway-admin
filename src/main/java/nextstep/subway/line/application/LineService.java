package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.stationAdapter.SafeStationAdapter;
import nextstep.subway.line.domain.stationAdapter.SafeStationInfo;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SafeStationAdapter safeStationAdapter;

    public LineService(
            LineRepository lineRepository, SafeStationAdapter safeStationAdapter
    ) {
        this.lineRepository = lineRepository;
        this.safeStationAdapter = safeStationAdapter;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        SafeStationInfo upStation = this.safeStationAdapter.getStationSafely(request.getUpStationId());
        SafeStationInfo downStation = this.safeStationAdapter.getStationSafely(request.getDownStationId());

        Line line = this.createLine(request.getName(), request.getColor(), request.getUpStationId(),
                request.getDownStationId(), request.getDistance());

        Line savedLine = lineRepository.save(line);

        return LineResponse.of(savedLine, Arrays.asList(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(it -> LineResponse.of(it, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));

        List<Long> stationIds = line.getStationIds();
        List<SafeStationInfo> safeStationInfos = safeStationAdapter.getStationsSafely(stationIds);

        return LineResponse.of(line, safeStationInfos);
    }

    // TODO: 향후에는 Station까지 변경될 경우 변경 예정
    @Transactional
    public Line updateLine(Long lineId, String changeName, String changeColor) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        Line updateLine = new Line(changeName, changeColor);
        line.update(updateLine);

        return line;
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public boolean addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line foundLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다"));

        return foundLine.addSection(
                new Section(
                        sectionRequest.getUpStationId(),
                        sectionRequest.getDownStationId(),
                        sectionRequest.getDistance())
        );
    }

    Line createLine(
            final String lineName, final String lineColor, final Long upStationId,
            final Long downStationId, final Long distance
    ) {
        Line line = new Line(lineName, lineColor);
        line.initFirstSection(upStationId, downStationId, distance);

        return line;
    }
}
