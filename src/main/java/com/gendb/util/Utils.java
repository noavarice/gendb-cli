package com.gendb.util;

public class Utils {

  public static void swap(final String[] args, final int first, final int second) {
    final String temp = args[first];
    args[first] = args[second];
    args[second] = temp;
  }
}
