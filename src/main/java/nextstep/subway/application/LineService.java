package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequestDTO;
import nextstep.subway.dto.LineResponseDTO;
import nextstep.subway.dto.LineResponsesDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponseDTO saveLine(LineRequestDTO lineRequestDTO){
        Line line = lineRequestDTO.toLine();
        Station upStation = stationRepository.findById(lineRequestDTO.getUpStaionId())
                .orElseThrow(() -> new IllegalArgumentException(""));
        Station downStation = stationRepository.findById(lineRequestDTO.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException(""));
        line.addStation(upStation);
        line.addStation(downStation);
        Line savedLine = lineRepository.save(line);
        return  LineResponseDTO.of(savedLine);
    }

    @Transactional
    public LineResponsesDTO findAll(){
        return null;
    }
}
