package nextstep.subway.line.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.NotFoundLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.NotFoundStationException;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        checkValidateLine(request);

        List<Station> stations = findByIdIn(Arrays.asList(request.getUpStationId(), request.getDownStationId()));

        Station upStation = findEqualStation(stations, request.getUpStationId());
        Station downStation = findEqualStation(stations, request.getDownStationId());

        Line saveLine = new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance());
        Line line = lineRepository.save(saveLine);
        return LineResponse.of(line);
    }

    private void checkValidateLine(LineRequest request) {
        if (isDuplicate(request.getName())) {
            throw new LineDuplicateException();
        }
    }

    private boolean isDuplicate(String name) {
        return lineRepository.findByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findId(Long id) {
        Line line = findById(id);
        return LineResponse.of(line);
    }

    public LineResponse update(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSections(Long lineId, SectionRequest request) {
        Line line = findById(lineId);

        List<Station> stations = findByIdIn(Arrays.asList(request.getUpStationId(), request.getDownStationId()));

        Station upStation = findEqualStation(stations, request.getUpStationId());
        Station downStation = findEqualStation(stations, request.getDownStationId());

        line.addSection(upStation, downStation, request.getDistance());

        return SectionResponse.of(line, Arrays.asList(upStation, downStation));
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findById(lineId);

        Station station = stationRepository.findById(stationId)
            .orElseThrow(NotFoundStationException::new);

        line.deleteSection(station);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(NotFoundLineException::new);
    }

    private List<Station> findByIdIn(List<Long> stationIds) {
        return stationRepository.findByIdIn(stationIds);
    }

    private Station findEqualStation(List<Station> stations, Long stationId) {
        return stations.stream()
            .filter(it -> it.isEqualId(stationId))
            .findAny()
            .orElseThrow(NotFoundStationException::new);
    }
}
