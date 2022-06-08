package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository, final LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        final SectionRequest sectionRequest = lineRequest.getSectionRequest();
        final Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        final Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        final LineStation savedLineStation = lineStationRepository.save(new LineStation(lineRequest.toLine() , new Section(upStation, downStation, sectionRequest.getDistance())));
        return LineResponse.of(savedLineStation.getLine(), Collections.singletonList(savedLineStation.getCurrentStation()));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream().map(line -> LineResponse.of(line, line.getLineStations().getStations())).collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        LineStation lineStation = lineStationRepository.findLineStationByLineId(id).orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(lineStation.getLine(), lineStation.getLine().getLineStations().getSortedStationsByStationId());
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest updateLine) {
        final Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        line.updateBy(updateLine);
    }

    @Transactional
    public SectionResponse saveSection(final Long id, final SectionRequest addSection) {
        final Station upStation = stationRepository.findById(addSection.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        final Station downStation = stationRepository.findById(addSection.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        final Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return SectionResponse.of(lineStationRepository.save(new LineStation(line, new Section(upStation, downStation, addSection.getDistance()))));
    }
}
