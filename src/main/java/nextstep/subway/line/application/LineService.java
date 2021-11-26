package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStationRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
        LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line persistLine = lineRepository
            .save(saveUpDownSection(request, upStation, downStation, request.toLine()));

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long lineId) {
        Line line = findById(lineId);
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lineList = lineRepository.findAll();
        return lineList
            .stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public Long updateLine(Long lineId, LineRequest lineRequest) {
        Line line = findById(lineId);

        line.update(lineRequest.toLine());
        Line save = lineRepository.save(line);
        return save.getId();
    }

    public void deleteLine(Long lineId) {
        Line line = findById(lineId);

        lineRepository.delete(line);
    }

    public LineResponse addSections(Long lineId, LineRequest lineRequest) {

        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        Line line = findById(lineId);

        line.addLineStation(Distance.valueOf(lineRequest.getDistance()), upStation, downStation);

        return LineResponse.of(line);
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(() -> new NotFoundException(Messages.NO_LINE.getValues()));
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new NotFoundException(Messages.NO_STATION.getValues()));
    }

    private LineStation saveSection(LineStation entity) {
        return lineStationRepository.save(entity);
    }

    private Line saveUpDownSection(LineRequest request, Station upStation, Station downStation, Line line) {
        saveSection(LineStation
            .ofUpLineStation(upStation, line, Distance.valueOf(request.getDistance()), downStation));
        saveSection(LineStation.fromDownLineStation(downStation, line));
        return line;
    }
}
