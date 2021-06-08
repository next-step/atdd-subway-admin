package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.Stations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {

        Section section = createSection(request);
        Line line = request.toLine();
        line.addSection(section);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Section createSection(LineRequest request) {
        Stations stations = findByIdIn(request.getUpStationId(), request.getDownStationId());
        Station upStation = stations.getStationById(request.getUpStationId());
        Station downStation = stations.getStationById(request.getDownStationId());

        return Section.create(upStation, downStation, request.getDistance());
    }

    private Stations findByIdIn(Long upStationId, Long downStationId) {
        return Stations.create(stationRepository.findByIdIn(Arrays.asList(upStationId, downStationId)));
    }

    private Station findStationById(long stationId) {
        return stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
    }

    public List<LineResponse> findLines() {
        List<Line> findLines = lineRepository.findAll();
        return findLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(long lineId) {
        return LineResponse.of(lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new));
    }

    @Transactional
    public void updateLine(long lineId, Line lineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);

        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public LineResponse addSection(long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);

        Stations stations = findByIdIn(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        Station upStation = stations.getStationById(sectionRequest.getUpStationId());
        Station downStation = stations.getStationById(sectionRequest.getDownStationId());

        Section section = Section.create(upStation, downStation, sectionRequest.getDistance());

        line.addSection(section);

        return LineResponse.of(line);
    }
}
