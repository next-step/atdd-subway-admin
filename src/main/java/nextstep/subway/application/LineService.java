package nextstep.subway.application;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.collection.LineStations;
import nextstep.subway.dto.LineRequestDTO;
import nextstep.subway.dto.LineResponseDTO;
import nextstep.subway.dto.LineResponsesDTO;
import nextstep.subway.dto.SectionRequestDTO;
import nextstep.subway.exception.CreateSectionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public LineResponseDTO saveLine(Station upStation, Station downStation, LineRequestDTO lineRequestDTO) {
        Line line = lineRepository.save(lineRequestDTO.toLine());
        LineStation lineStation = lineStationRepository.save(new LineStation(line, upStation, downStation, lineRequestDTO.getDistance()));
        line.addLineStation(lineStation);
        return LineResponseDTO.of(line);
    }

    @Transactional(readOnly = true)
    public LineResponsesDTO findAll() {
        List<Line> lines = lineRepository.findAllByDeletedFalse();
        return LineResponsesDTO.of(lines);
    }

    @Transactional(readOnly = true)
    public LineResponseDTO findOne(Long lineId) {
        Line line = getLine(lineId);
        return LineResponseDTO.of(line);
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = getLine(lineId);
        line.delete();
    }

    @Transactional
    public void updateLineInfo(Long id, LineRequestDTO lineRequestDTO) {
        Line line = getLine(id);
        line.update(lineRequestDTO.getName(), lineRequestDTO.getColor());
    }

    private Line getLine(Long lineId) {
        return lineRepository.findByIdAndDeletedFalse(lineId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] ID에 해당하는 노선이 없습니다."));
    }

    @Transactional
    public void addSection(Long id, Station upStation, Station downStation, SectionRequestDTO sectionRequestDTO) {
        Line line = getLine(id);
        LineStations lineStations = line.getLineStations();
        boolean isCreatedSection = lineStations.addSection(line, upStation, downStation, sectionRequestDTO.getDistance());
        if(!isCreatedSection){
            throw new CreateSectionException("[ERROR] 등록할 구간을 찾을 수 없습니다.");
        }
    }
}
