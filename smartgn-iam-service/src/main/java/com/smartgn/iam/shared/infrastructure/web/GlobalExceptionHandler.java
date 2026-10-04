package com.smartgn.iam.shared.infrastructure.web;

import com.smartgn.iam.auth.domain.exception.CredencialesInvalidasException;
import com.smartgn.iam.auth.domain.exception.EmailYaRegistradoException;
import com.smartgn.iam.auth.domain.exception.RolNoPermitidoException;
import com.smartgn.iam.auth.domain.exception.TokenRecuperacionInvalidoException;
import com.smartgn.iam.profile.domain.exception.AccesoAPerfilDenegadoException;
import com.smartgn.iam.profile.domain.exception.PerfilNoEncontradoException;
import com.smartgn.iam.profile.domain.exception.PerfilYaExisteException;
import com.smartgn.iam.subscription.domain.exception.SuscripcionNoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- auth ----

    @ExceptionHandler(CredencialesInvalidasException.class)
    ProblemDetail credencialesInvalidas(CredencialesInvalidasException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    ProblemDetail emailDuplicado(EmailYaRegistradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RolNoPermitidoException.class)
    ProblemDetail rolNoPermitido(RolNoPermitidoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(TokenRecuperacionInvalidoException.class)
    ProblemDetail tokenRecuperacionInvalido(TokenRecuperacionInvalidoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ---- profile ----

    @ExceptionHandler(PerfilNoEncontradoException.class)
    ProblemDetail perfilNoEncontrado(PerfilNoEncontradoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PerfilYaExisteException.class)
    ProblemDetail perfilYaExiste(PerfilYaExisteException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccesoAPerfilDenegadoException.class)
    ProblemDetail accesoDenegado(AccesoAPerfilDenegadoException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ---- subscription ----

    @ExceptionHandler(SuscripcionNoEncontradaException.class)
    ProblemDetail suscripcionNoEncontrada(SuscripcionNoEncontradaException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---- general ----

    // Fallback for a race between two simultaneous requests (duplicate email or profile)
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail violacionDeIntegridad(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The resource already exists or violates a data constraint");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid data");
        problem.setProperty("errors", errores);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail cuerpoIlegible(HttpMessageNotReadableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid request body. Check the JSON and the values sent.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "The parameter '" + ex.getName() + "' has an invalid format");
    }
}
