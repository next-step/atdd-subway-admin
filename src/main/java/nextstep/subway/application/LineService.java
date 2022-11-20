package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.LineStationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        List<Station> stations = stationService.findStationByIdIn(
                Arrays.asList(lineRequest.getUpStationId(), lineRequest.getDownStationId()));
        Station upStation = getStationInStations(stations, lineRequest.getUpStationId());
        Station downStation = getStationInStations(stations, lineRequest.getDownStationId());

        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        line.initLineStations(Arrays.asList(
                lineStationRepository.save(new LineStation(null, upStation, 0, line)),
                lineStationRepository.save(new LineStation(upStation, downStation, lineRequest.getDistance(), line)),
                lineStationRepository.save(new LineStation(downStation, null, 0, line))
        ));

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", lineId))
        );

        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", lineId))
        );

        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", lineId))
        );

        lineRepository.delete(line);
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", lineId))
        );

        List<Station> stations = stationService.findStationByIdIn(
                Arrays.asList(sectionRequest.getUpStationId(), sectionRequest.getDownStationId()));
        Station upStation = getStationInStations(stations, sectionRequest.getUpStationId());
        Station downStation = getStationInStations(stations, sectionRequest.getDownStationId());
        LineStation lineStation = new LineStation(upStation, downStation, sectionRequest.getDistance(), line);

        line.addLineStation(lineStation);

        return LineResponse.of(line);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(
                () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", lineId))
        );
        Station station = stationService.findStationById(stationId);
        line.deleteLineStation(station);
    }

    private Station getStationInStations(List<Station> stations, Long stationId) {
        return stations.stream()
                .filter(station -> Objects.equals(station.getId(), stationId))
                .findFirst()
                .orElseThrow(
                        () -> new LineStationNotFoundException(String.format("존재하지 않는 지하철 노선입니다. (id : %s)", stationId))
                );
    }
}
