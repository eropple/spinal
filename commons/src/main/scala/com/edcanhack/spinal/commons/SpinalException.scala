package com.edcanhack.spinal.commons

/**
 * Created by ed on 7/31/14.
 */
class SpinalException(message: String, throwable: Throwable) extends RuntimeException(message, throwable) {
  def this(message: String, args: AnyRef*) = this(String.format(message, args), null)
  def this(throwable: Throwable, message: String, args: AnyRef*) = this(String.format(message, args), throwable)
}
