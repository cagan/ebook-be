package com.cagan.library.web.errors;

import com.cagan.library.web.errors.BadRequestAlertException;
import com.cagan.library.web.errors.ErrorConstants;

import java.io.Serial;

public class FileNotExceptedException extends BadRequestAlertException {
    @Serial
    private static final long serialVersionUID = 1L;
    public FileNotExceptedException(String message) {
//        super("File can not be empty");
        super(ErrorConstants.FILE_NOT_EXCEPTED_EXCEPTION, message, "Book", "FILE_NOT_EXCEPTED");
    }
}
