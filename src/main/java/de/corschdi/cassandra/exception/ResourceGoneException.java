package de.corschdi.cassandra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class ResourceGoneException extends RuntimeException {
}
