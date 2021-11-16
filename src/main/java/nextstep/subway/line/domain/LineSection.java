package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.exception.SectionNotFoundException;

@Entity
public class LineSection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Section section;

	protected LineSection() {
	}

	public LineSection(Line line, Section section) {
		validate(line, section);
		this.line = line;
		this.section = section;
	}

	private void validate(Line line, Section section) {
		if (null == line) {
			throw new LineNotFoundException();
		}
		if (null == section) {
			throw new SectionNotFoundException();
		}
	}

	public boolean isUpTerminal() {
		return section.isUpTerminal();
	}

	public boolean isDownTerminal() {
		return section.isDownTerminal();
	}

	public Long getId() {
		return id;
	}

	public Line getLine() {
		return line;
	}

	public Section getSection() {
		return section;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof LineSection))
			return false;
		LineSection that = (LineSection)o;
		return Objects.equals(id, that.id) && Objects.equals(line, that.line)
			&& Objects.equals(section, that.section);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, line, section);
	}
}
