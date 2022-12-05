package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.line.LineStationRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.UpdateLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private LineRepository lineRepository;
    private LineStationRepository lineStationRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository,
            StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> findResults = lineRepository.findAll();
        return findResults.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(lineRepository.findById(id).get());
    }

    @Transactional
    public LineResponse updateLine(Long id, UpdateLine request) {
        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        line.update(request);
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        //노선에 지하철역 등록
        Station station = stationRepository.findById(sectionRequest.getDownStationId()).get();
        Station preStation = stationRepository.findById(sectionRequest.getUpStationId()).get();
        Line line = lineRepository.findById(lineId).get();
        LineStation lineStation = new LineStation(station, preStation, sectionRequest.getDistance(), line);
        LineStation persistLineStation = lineStationRepository.save(lineStation);
        return SectionResponse.of(persistLineStation);
    }

    public List<SectionResponse> findLineStationsByLineId(Long id) {
        Line line = lineRepository.findById(id).get();
        List<LineStation> findResults = lineStationRepository.findByLine(line);
        return findResults.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
