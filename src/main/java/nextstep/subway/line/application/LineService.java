package nextstep.subway.line.application;

import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SafeStationDomainService safeStationDomainService;

    public LineService(
            LineRepository lineRepository, SafeStationDomainService safeStationDomainService
    ) {
        this.lineRepository = lineRepository;
        this.safeStationDomainService = safeStationDomainService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        SafeStationInfo upStation = this.safeStationDomainService.getStationSafely(request.getUpStationId());
        SafeStationInfo downStation = this.safeStationDomainService.getStationSafely(request.getDownStationId());

        Line line = this.createLine(request.getName(), request.getColor(), request.getUpStationId(),
                request.getDownStationId(), request.getDistance());

        Line savedLine = lineRepository.save(line);

        return LineResponse.of(savedLine, Arrays.asList(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(it -> LineResponse.of(it))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));

        return LineResponse.of(line);
    }

    // TODO: 향후에는 Station까지 변경될 경우 변경 예정
    public Line updateLine(Long lineId, String changeName, String changeColor) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        Line updateLine = new Line(changeName, changeColor);
        line.update(updateLine);

        return line;
    }

    public void deleteLine(Long lineId) {
        lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException("해당 라인이 존재하지 않습니다."));
        lineRepository.deleteById(lineId);
    }

    Line createLine(
            final String lineName, final String lineColor, final Long upStationId,
            final Long downStationId, final Long distance
    ) {
        Line line = new Line(lineName, lineColor);
        line.addNewSection(upStationId, downStationId, distance);

        return line;
    }
}
