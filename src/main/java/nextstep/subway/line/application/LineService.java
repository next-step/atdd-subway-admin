package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllStations() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(final Long lineId) {
        Line id = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));

        List<Station> stations = id.getStations();

        return LineResponse.of(id, stations);
    }

    public LineResponse saveLine(final LineRequest request) {
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(() -> new DataIntegrityViolationException("Not Fount downStationId" + request.getDownStationId()));
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(() -> new DataIntegrityViolationException("Not Fount downStationId" + request.getUpStationId()));
        int distance = request.getDistance();

        Line line = request.toLine();
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);

        Line persistLine = lineRepository.save(line);

        List<Station> stations = persistLine.getStations();

        return LineResponse.of(persistLine, stations);
    }

    public LineResponse updateById(final Long lineId,final LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

}
