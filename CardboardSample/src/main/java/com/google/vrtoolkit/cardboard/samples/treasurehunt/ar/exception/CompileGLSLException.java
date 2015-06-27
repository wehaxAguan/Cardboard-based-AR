package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.exception;

/**
 * Created by mayuhan on 15/6/27.
 * 编译GLSL时抛出的异常
 */
public class CompileGLSLException extends RuntimeException {
    public CompileGLSLException(String detail) {
        super(detail);
    }
}
