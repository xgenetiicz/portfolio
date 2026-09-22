package com.example.genetiicz.Exceptions;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptions  {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String>handleMaxFileLimit(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(413).body("Maximum size exceeded. Maximum size allowed is 50MB, please try again");
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String>couldNotUploadFile(FileUploadException exception){
        return ResponseEntity.status(500).body("Could not upload file, please try again");
    }
    /*

    This is maybe, maybe the goated thing i have ever seen, a way to handle all THOSE exceptions in one class where SPRING AUTOMATICALLY FINDS THEN AND
    RETURN VALUE TO USER WITH CORRECT STATUS AND MESSAGE. WHAT THE F. this got 10 times just easier.
    I WOULD NEVER USE TRY CATCH AGAIN IN CODE BLOCKS HAHAHAHA!!!!
    */

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<String>notAuthorized(HttpClientErrorException.Unauthorized exception) {
        return ResponseEntity.status(403).body("You are not authorized to make this request - contact administrator.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String>notAuthenticated(AuthenticationException exception) {
        return ResponseEntity.status(401).body("You are not authenticated for this request");
    }
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<String> projectNotFound(ProjectNotFoundException exception) {
        return ResponseEntity.status(404).body(exception.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<String> notAuthorized(NotAuthorizedException exception) {
        return ResponseEntity.status(401).body(exception.getMessage());
    }

    @ExceptionHandler(ServerResourceException.class)
        public ResponseEntity<String> resourceNotDeleted(ServerResourceException exception) {
        return ResponseEntity.status(500).body(exception.getMessage() + "Could not delete content/files on server level");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> roleNotFound(RoleNotFoundException exception) {
        return ResponseEntity.status(500).body(exception.getMessage());
    }

    //Creating a MethodArgumentNotValidException -  this is because the response from each feature is not giving correct response based on what is happening within the application.
    // The validations are happening, but with the wrong response.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String>notValid(MethodArgumentNotValidException exception) {

        List<FieldError> errorList = exception.getBindingResult().getFieldErrors();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : errorList){
            if(!errorMessage.isEmpty()){
                errorMessage.append(", \n"); //for each error that is not EMPTY - we append ", \newline"
            }
            errorMessage.append(error.getDefaultMessage());
        }
        return ResponseEntity.status(400).body(errorMessage.toString());
    }

    //ExceptionHandler for DuplicateProjectURL
    @ExceptionHandler(DuplicateProjectURLException.class)
    public ResponseEntity<String>notValidURL(DuplicateProjectURLException exception) {
        return ResponseEntity.status(400).body(exception.getMessage());
    }
}
