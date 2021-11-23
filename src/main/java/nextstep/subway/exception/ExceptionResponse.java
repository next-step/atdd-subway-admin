package nextstep.subway.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {

	private String message;
	private String description;
	private LocalDateTime createdDate;
	private HttpStatus httpStatus;

	public ExceptionResponse(HttpStatus httpStatus, String message, String description, LocalDateTime createdDate) {
		this.httpStatus = httpStatus;
		this.message = message;
		this.description = description;
		this.createdDate = createdDate;
	}

	public ExceptionResponse(AppException appException) {
		this.message = appException.getErrorCode().getMessage();
		this.httpStatus = appException.getErrorCode().getHttpStatus();
		this.description = appException.getDescription();
		this.createdDate = LocalDateTime.now();
	}

	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
}
