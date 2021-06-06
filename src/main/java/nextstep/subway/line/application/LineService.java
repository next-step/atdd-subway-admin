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

import java.util.List;
import java.util.stream.Collectors;

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
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));

        return LineResponse.of(line);
    }

    public LineResponse saveLine(final LineRequest request) {

        Line line = request.toLine();
        line.addSection(new Section(getStation(request.getUpStationId()), getStation(request.getDownStationId()),
                request.getDistance()));

        Line save = lineRepository.save(line);
        return LineResponse.of(save);
    }

    public LineResponse updateById(final Long lineId, final LineRequest lineRequest) {
        Line originLine = lineRepository.findById(lineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Not Found lineId" + lineId));

        Section section = new Section(getStation(lineRequest.getUpStationId()), getStation(lineRequest.getDownStationId()),
                lineRequest.getDistance());
        originLine.update(lineRequest.getName(), lineRequest.getColor(), section);

        return LineResponse.of(originLine);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new DataIntegrityViolationException("Not Fount downStationId" + stationId));
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
