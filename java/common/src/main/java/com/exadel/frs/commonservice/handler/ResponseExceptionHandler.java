package com.exadel.frs.commonservice.handler;

import com.exadel.frs.commonservice.dto.ExceptionResponseDto;
import com.exadel.frs.commonservice.exception.BasicException;
import com.exadel.frs.commonservice.exception.ConstraintViolationException;
import com.exadel.frs.commonservice.exception.EmptyRequiredFieldException;
import com.exadel.frs.commonservice.exception.PatternMatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.exadel.frs.commonservice.handler.CommonExceptionCode.UNDEFINED;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseExceptionHandler {

    private enum LogLevel {
        ERROR, DEBUG
    }

    @ExceptionHandler(BasicException.class)
    public ResponseEntity<ExceptionResponseDto> handleDefinedExceptions(final BasicException ex) {
        switch (ex.getLogLevel()) {
            case ERROR:
                log.error("Defined exception occurred", ex);
                break;
            case DEBUG:
                log.debug("Defined exception occurred", ex);
                break;
        }

        return ResponseEntity
                .status(ex.getExceptionCode().getHttpStatus())
                .body(buildBody(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.error("Method argument not valid exception occurred", ex);

        final BasicException basicException = getBasicException(ex);

        return ResponseEntity
                .status(basicException.getExceptionCode().getHttpStatus())
                .body(buildBody(basicException));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleUndefinedExceptions(final Exception ex) {
        log.error("Undefined exception occurred", ex);

        final BasicException basicException = new BasicException(UNDEFINED, ex.getMessage());

        return ResponseEntity
                .status(basicException.getExceptionCode().getHttpStatus())
                .body(buildBody(basicException));
    }

    private ExceptionResponseDto buildBody(final BasicException ex) {
        return ExceptionResponseDto.builder()
                .code(ex.getExceptionCode().getCode())
                .message(ex.getMessage())
                .build();
    }

    private BasicException getBasicException(final MethodArgumentNotValidException ex) {
        final FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            return new BasicException(UNDEFINED, "");
        }

        final String code = fieldError.getCode();
        BasicException basicException;

        if ("NotBlank".equals(code) || "ValidEnum".equals(code) || "Size".equals(code)) {
            basicException = new ConstraintViolationException(fieldError.getDefaultMessage());
        } else if ("NotNull".equals(code) || "NotEmpty".equals(code)) {
            basicException = new EmptyRequiredFieldException(fieldError.getField());
        } else if ("Pattern".equals(code)) {
            basicException = new PatternMatchException(fieldError.getDefaultMessage());
        } else {
            basicException = new BasicException(UNDEFINED, "");
        }

        return basicException;
    }
}
