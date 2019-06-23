package com.app.jonathanchiou.willimissbart.utils

import java.lang.Exception

/**
 * For zipping ui multiple UI models into one, we want a clean, concise way to capture all the throwable information.
 * So I created this class to effectively swallow all those exceptions and put it into 1 object.
 */
class GulpException(throwables: List<Throwable?>): Exception()