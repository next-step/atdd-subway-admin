package nextstep.subway.line.application;

import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.SectionExistException;
import nextstep.subway.exception.StationNotContainInUpOrDownStation;
import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line line = lineRepository.save(request.toLine(upStation, downStation, request.getDistance()));
        return LineResponse.of(line);
    }

    private Station findStationById(Long upStationId) {
        return stationRepository.findById(upStationId)
                .orElseThrow(StationNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line line = findLineById(id);
        line.update(lineUpdateRequest.toLine());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    public LineResponse addLineSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        if (line.hasStation(upStation) && line.hasStation(downStation)) {
            throw new SectionExistException();
        }

        if (line.hasStation(upStation)) {
            line.updateUpStation(upStation, downStation, sectionRequest.getDistance());
            line.addSection(upStation, downStation, sectionRequest.getDistance());
            return LineResponse.of(line);
        }
        if (line.hasStation(downStation)) {
            line.updateDownStation(upStation, downStation, sectionRequest.getDistance());
            line.addSection(upStation, downStation, sectionRequest.getDistance());
            return LineResponse.of(line);
        }

        throw new StationNotContainInUpOrDownStation();
    }
}
