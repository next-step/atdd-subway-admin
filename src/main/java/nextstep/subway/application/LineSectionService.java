package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.SectionInvalidException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineSectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final LineStationRepository lineStationRepository;

    public LineSectionService(LineRepository lineRepository, StationRepository stationRepository,
        SectionRepository sectionRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional()
    public void deleteStation(Long lineId, Long stationId){
        Line line = lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
        Station station = stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
        if(line.getUpStation().equals(station)){
            changeUpStation(line, station);

        }
        else if(line.getDownStation().equals(station)){
            changeDownStation(line, station);

        }else {
            mergeSections(line, station);
        }

    }

    private void mergeSections(Line line, Station station) {
        Section backSection = sectionRepository.findAllByUpStationAndLine(station, line)
            .orElseThrow(LineNotFoundException::new);
        Section nextSection = sectionRepository.findAllByDownStationAndLine(station, line)
            .orElseThrow(LineNotFoundException::new);
        line.replaceSectionByMerge(backSection, nextSection);

        sectionRepository.delete(backSection);
        sectionRepository.delete(nextSection);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }


    private void changeDownStation(Line line, Station station) {
        Section section = sectionRepository.findAllByDownStationAndLine(station, line)
            .orElseThrow(SectionInvalidException::new);
        line.changeDownStationForDelete(section);
        sectionRepository.delete(section);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }

    private void changeUpStation(Line line, Station station) {
        Section section = sectionRepository.findAllByUpStationAndLine(station, line)
            .orElseThrow(SectionInvalidException::new);
        line.changeUpStationForDelete(section);
        sectionRepository.delete(section);
        lineStationRepository.deleteAllByStationAndLine(station, line);
    }

}
