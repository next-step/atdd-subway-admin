package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.lineStation.domain.LineStationRepository;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        LineStation upLineStation = new LineStation();
        persistLine.addLineStation(upLineStation);
        upStation.addLineStation(upLineStation);

        LineStation downLineStation = new LineStation();
        persistLine.addLineStation(downLineStation);
        downStation.addLineStation(downLineStation);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
            Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.updateLine(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }
}
