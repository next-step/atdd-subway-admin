package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

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
        Line persistLine = lineRepository.save(request.toLine(
            stationRepository.findById(request.getUpStationId()).orElse(null),
            stationRepository.findById(request.getDownStationId()).orElse(null)
        ));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

	public LineResponse findOne(Long id) {
        return lineRepository.findById(id)
            .map(LineResponse::of)
            .orElseThrow(NoSuchElementException::new);
	}

    public void update(Long id, LineRequest updated) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);

        line.changeName(updated.getName());
        line.changeColor(updated.getColor());

        Station upStation = stationRepository.findById(updated.getUpStationId()).orElse(null);
        Station downStation = stationRepository.findById(updated.getDownStationId()).orElse(null);
        line.removeAll();
        line.addOrUpdateStation(upStation, downStation, updated.getDistance());
        line.addOrUpdateStation(downStation);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}