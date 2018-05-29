package com.gendb.util;

import java.util.HashMap;
import java.util.Map;

public enum ArgumentTypes {

  HELP,
  CONFIG_PATH,
  OUTPUT_SCRIPT_PATH,
  CONNECTION_PROPERTIES_PATH,
  ;

  private static final Map<String, ArgumentTypes> ARGUMENT_TO_TYPE = new HashMap<String, ArgumentTypes>() {{
    put("-h", HELP);
    put("--help", HELP);
    put("-c", CONFIG_PATH);
    put("--config-path", CONFIG_PATH);
    put("-o", OUTPUT_SCRIPT_PATH);
    put("--output-path", OUTPUT_SCRIPT_PATH);
    put("-p", CONNECTION_PROPERTIES_PATH);
    put("--conn-props-path", CONNECTION_PROPERTIES_PATH);
  }};

  public static ArgumentTypes fromArg(final String arg) {
    if (!ARGUMENT_TO_TYPE.containsKey(arg)) {
      throw new IllegalArgumentException("Unknown command-line argument: " + arg);
    }

    return ARGUMENT_TO_TYPE.get(arg);
  }
}
