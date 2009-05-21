/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.37
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.xuggle.ferry;

/**
 * Internal Only  
 * <p>  
 * C++ wrapper to SLF4J Java Logging frame work.  
 * </p>  
 * <p>  
 * If not running inside a JVM, then this class  
 * just does a rudimentary printout of log messages  
 * to stderr.  
 * </p>  
 * <p>  
 * Otherwise, it forwards to Java's SLF4J logging framework.  
 * </p>  
 */
public class Logger {
  // JNIHelper.swg: Start generated code
  // >>>>>>>>>>>>>>>>>>>>>>>>>>>

  private volatile long swigCPtr;
  protected boolean swigCMemOwn;
  @SuppressWarnings("unused")
  private JNINativeFinalizer mUnusedVariableToAllowImports;
  
  /**
   * Internal Only.  
   * 
   * DO NOT USE: Do not allocate this method using new.  Not part of public API.
   * <p>
   * Unfortunately this constructor is public because the internal
   * implementation needs it to be, but do not pass in values to this method
   * as you may end up crashing the virtual machine.
   * </p>
   *
   * @param cPtr A C pointer to direct memory; did we mention don't call this.
   * @param cMemoryOwn I'm not even going to tell you.  Stop it.  Go away.
   *
   */ 
  protected Logger(long cPtr, boolean cMemoryOwn) {
    swigCPtr = cPtr;
    swigCMemOwn = cMemoryOwn;
  }

  /**
   * Internal Only.  Not part of public API.
   *
   * Get the raw value of the native object that obj is proxying for.
   *   
   * @param obj The java proxy object for a native object.
   * @return The raw pointer obj is proxying for.
   */
  public static long getCPtr(Logger obj) {
    if (obj == null) return 0;
    return obj.getMyCPtr();
  }
  
  /**
   * Internal Only.  Not part of public API.
   *
   * Get the raw value of the native object that we're proxying for.
   *   
   * @return The raw pointer we're proxying for.
   */  
  public long getMyCPtr() {
    if (swigCPtr == 0) throw new IllegalStateException("underlying native object already deleted");
    return swigCPtr;
  }

  /**
   * Compares two values, returning true if the underlying objects in native code are the same object.
   *
   * That means you can have two different Java objects, but when you do a comparison, you'll find out
   * they are the EXACT same object.
   *
   * @return True if the underlying native object is the same.  False otherwise.
   */
  public boolean equals(Object obj) {
    boolean equal = false;
    if (obj instanceof Logger)
      equal = (((Logger)obj).swigCPtr == this.swigCPtr);
    return equal;
  }
  
  /**
   * Get a hashable value for this object.
   *
   * @return the hashable value.
   */
  public int hashCode() {
     return (int)swigCPtr;
  }
  
  /**
   * Finalize this object.  Note this should only exists on non RefCounted objects.
   */
  protected void finalize()
  {
    delete();
  }

  // <<<<<<<<<<<<<<<<<<<<<<<<<<<
  // JNIHelper.swg: End generated code
  /**
   * Releases any underlying native memory and marks this object
   * as invalid.
   * <p>
   * Normally Ferry manages when to release native memory.
   * </p>
   * <p>
   * In the unlikely event you want to control EXACTLY when a native 
   * object is released, each Xuggler object has a {@link #delete()}
   * method that you can use. Once you call {@link #delete()},
   * you must ENSURE your object is never referenced again from
   * that Java object -- Ferry tries to help you avoid crashes if you
   * accidentally use an object after deletion but on this but we
   * cannot offer 100% protection (specifically if another thread
   *  is accessing that object EXACTLY when you {@link #delete()} it). 
   * </p>
   */
  

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      FerryJNI.delete_Logger(swigCPtr);
    }
    swigCPtr = 0;
  }

/**
 * Returns a new Logger object for this loggerName.  
 * @param	aLoggerName A name (no spaces allowed) for this logger.  
 *  
 */
  public static Logger getLogger(String aLoggerName) {
    long cPtr = FerryJNI.Logger_getLogger(aLoggerName);
    return (cPtr == 0) ? null : new Logger(cPtr, false);
  }

/**
 * Get a Logger object, but ask the Logger code to  
 * free it up once the JavaVM shuts down. Use at your  
 * own risk.  
 * @param	aLoggerName A name (no spaces allowed) for this logger.  
 * @return	A new logger for logging; caller must not call delete  
 * and must not use the logger once the JavaVM (or main) has exited. 
 *  
 */
  public static Logger getStaticLogger(String aLoggerName) {
    long cPtr = FerryJNI.Logger_getStaticLogger(aLoggerName);
    return (cPtr == 0) ? null : new Logger(cPtr, false);
  }

/**
 * Log the message to the logger, using sprintf() format  
 * strings.  
 * @param	filename The filename that is logging, or NULL.  
 * @param	lineNo The line number where this log statement is executed 
 *		 from.  
 * or 0.  
 * @param	level Level to log at.  
 *  
 * @return	if the message was actually logged.  
 */
  public boolean log(String filename, int lineNo, Logger.Level level, String format) {
    return FerryJNI.Logger_log(swigCPtr, this, filename, lineNo, level.swigValue(), format);
  }

  public boolean error(String filename, int lineNo, String format) {
    return FerryJNI.Logger_error(swigCPtr, this, filename, lineNo, format);
  }

  public boolean warn(String filename, int lineNo, String format) {
    return FerryJNI.Logger_warn(swigCPtr, this, filename, lineNo, format);
  }

  public boolean info(String filename, int lineNo, String format) {
    return FerryJNI.Logger_info(swigCPtr, this, filename, lineNo, format);
  }

  public boolean debug(String filename, int lineNo, String format) {
    return FerryJNI.Logger_debug(swigCPtr, this, filename, lineNo, format);
  }

  public boolean trace(String filename, int lineNo, String format) {
    return FerryJNI.Logger_trace(swigCPtr, this, filename, lineNo, format);
  }

  public boolean isLogging(Logger.Level level) {
    return FerryJNI.Logger_isLogging(swigCPtr, this, level.swigValue());
  }

  public void setIsLogging(Logger.Level level, boolean value) {
    FerryJNI.Logger_setIsLogging(swigCPtr, this, level.swigValue(), value);
  }

  public static boolean isGlobalLogging(Logger.Level level) {
    return FerryJNI.Logger_isGlobalLogging(level.swigValue());
  }

  public static void setGlobalIsLogging(Logger.Level level, boolean value) {
    FerryJNI.Logger_setGlobalIsLogging(level.swigValue(), value);
  }

  public String getName() {
    return FerryJNI.Logger_getName(swigCPtr, this);
  }

  public enum Level {
  /**
   * Different logging levels (noiseness) supported by us.
   */
    LEVEL_ERROR(FerryJNI.Logger_LEVEL_ERROR_get()),
    LEVEL_WARN(FerryJNI.Logger_LEVEL_WARN_get()),
    LEVEL_INFO(FerryJNI.Logger_LEVEL_INFO_get()),
    LEVEL_DEBUG(FerryJNI.Logger_LEVEL_DEBUG_get()),
    LEVEL_TRACE(FerryJNI.Logger_LEVEL_TRACE_get());

    public final int swigValue() {
      return swigValue;
    }

    public static Level swigToEnum(int swigValue) {
      Level[] swigValues = Level.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (Level swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + Level.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private Level() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private Level(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private Level(Level swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

}
