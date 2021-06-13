package nextstep.subway.line.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.linestation.domain.LineStation;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(final LineRequest request) {
        return lineResponse(persistLine(initializeLine(request)));
    }

    private Line initializeLine(final LineRequest request) {
        final Line line = request.toLine();
        line.addLineStations(lineStations(request, line));

        return line;
    }

    private List<LineStation> lineStations(final LineRequest request, final Line line) {
        final Station upStation = station(request.getUpStationId());
        final Station downStation = station(request.getDownStationId());

        final LineStation lineStation = new LineStation(line, upStation);
        lineStation.next(downStation, request.getDistance());
        final LineStation lineStation2 = new LineStation(line, downStation);
        lineStation2.previous(upStation, request.getDistance());

        return Arrays.asList(lineStation, lineStation2);
    }

    private Line persistLine(final Line line) {
        return lineRepository.save(line);
    }

    private LineResponse lineResponse(final Line line) {
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        final List<Line> lines = findAllLine();
        final List<LineResponse> lineResponses = new ArrayList<>(lines.size());
        lines.forEach(line -> lineResponses.add(lineResponse(line)));

        return lineResponses;
    }

    private List<Line> findAllLine() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(final Long id) {
        return LineResponse.of(findById(id));
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundException::new);
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = findById(id);
        line.update(lineRequest.toLine());
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Line line = findById(lineId);
        final Station upStation = station(sectionRequest.getUpStationId());
        final Station downStation = station(sectionRequest.getDownStationId());
        line.addLineStation(upStation, downStation, sectionRequest.getDistance());

        return LineResponse.of(line);
    }

    private Station station(final Long upStationId) {
        return stationRepository.findById(upStationId)
            .orElseThrow(NotFoundException::new);
    }
}
