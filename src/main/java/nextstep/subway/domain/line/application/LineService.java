package nextstep.subway.domain.line.application;

import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.domain.LineRepository;
import nextstep.subway.domain.line.dto.LineRequest;
import nextstep.subway.domain.line.dto.LineResponse;
import nextstep.subway.domain.line.mapper.LineMapper;
import nextstep.subway.domain.section.domain.Section;
import nextstep.subway.domain.station.application.StationService;
import nextstep.subway.domain.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        final Station upStation = stationService.findStation(request.getUpStationId());
        final Station downStation = stationService.findStation(request.getDownStationId());

        Section.createFirstSection(upStation, downStation, request.getDistance(), persistLine);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLine() {
        final List<Line> lines = lineRepository.findAll();
        final List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(LineResponse.of(line, LineMapper.sectionToLineStation(line)));
        }

        return lineResponses;
    }

    public LineResponse modifyLine(final Long lineId, final LineRequest lineRequest) {
        final Line findLine = findLine(lineId);
        findLine.update(new Line(lineRequest.getName(), lineRequest.getColor()));

        return LineResponse.of(findLine);
    }

    public void deleteLine(final Long lineId) {
        final Line findLine = findLine(lineId);
        lineRepository.delete(findLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findDetailLine(final Long lineId) {
        final Line line = this.findLine(lineId);
        return LineResponse.of(line, LineMapper.sectionToLineStation(line));
    }

    @Transactional(readOnly = true)
    public Line findLine(final Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 노선은 존재하지 않습니다."));
    }
}
