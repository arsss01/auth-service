package air.astana.authservice.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        ex.printStackTrace();

        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        return ResponseEntity.status(err.getStatus()).body(err);
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Object> handleGlobalException(GlobalException ex, WebRequest request) {
        logger.error("exceptionId: " + ex.exceptionId);
        logger.error("exception errorDetails: " + ex.errorDetails);

        ex.printStackTrace();

        ApiError err = new ApiError(ex.exceptionId, HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(), request.getContextPath(), ex.errorDetails);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        logger.error("exceptionId: " + ex.exceptionId);
        logger.error("exception errorDetails: " + ex.errorDetails);

        ex.printStackTrace();
        ApiError err = new ApiError(ex.exceptionId, HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getContextPath(),
                ex.errorDetails);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    // triggers when the JSON is invalid
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        Map<String, Object> details = new HashMap<>();

        details.put("Not supported media type", ex.getContentType());
        details.put("Supported media types are", ex.getSupportedMediaTypes());

        ApiError err = new ApiError(HttpStatus.BAD_REQUEST, request.getContextPath(),
                "Invalid JSON", details);

        return ResponseEntity.status(status).body(err);
    }


    // triggers when the JSON is malformed
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("error msg", ex.getMessage());

        ApiError err = new ApiError(HttpStatus.BAD_REQUEST, request.getContextPath(),
                "Malformed JSON request", details);


        return ResponseEntity.status(status).body(err);
    }

    // triggers when @Valid fails
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError it : ex.getBindingResult().getFieldErrors()) {
            details.computeIfPresent(it.getField(), (key, value) -> value + ", " + it.getDefaultMessage());
            details.computeIfAbsent(it.getField(), value -> it.getDefaultMessage());
        }

        ApiError err = new ApiError(HttpStatus.BAD_REQUEST, request.getContextPath(),
                "Validation Errors", details);

        return ResponseEntity.status(status).body(err);
    }

    // triggers when there are missing parameters
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> details = new HashMap();
        details.put("parameter is missing", ex.getParameterName());

        ApiError err = new ApiError(HttpStatus.BAD_REQUEST, request.getContextPath(),
                "Missing Parameters", details);

        return ResponseEntity.status(status).body(err);
    }
}
