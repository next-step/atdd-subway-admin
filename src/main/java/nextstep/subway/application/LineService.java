package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse readLine(Long id) {
        return LineResponse.from(lineRepository.findById(id).get());
    }

    public List<LineResponse> readLines() {
        return lineRepository.findAll().stream().map(LineResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).get();

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation,
                lineRequest.getDistance());

        Line persistLine = lineRepository.save(line);
        return LineResponse.from(lineRepository.findById(persistLine.getId()).get());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        line.update(lineRequest);
    }

    @Transactional
    public void addLineStation(long lineId, SectionRequest sectionRequest){
        Line line = lineRepository.findById(lineId).get();
        line.addLineStation(sectionRequest);
    }
}
