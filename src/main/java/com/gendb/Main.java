package com.gendb;

public class Main {

  private static final String HELP = String.format("Gendb fills realtional databases with test data%1$s"
      + "gendb [-c|--config-path] CONFIG_FILE_PATH "
      + "([-o|--output-path] OUTPUT_FILE_PATH) | "
      + "([-p|--conn-props-path] CONNECTION_PROPERTIES_FILE_PATH)%1$s%1$s"
      + "Details:%1$s"
      + "-c, --config-path - path to the configuration file%1$s"
      + "-o, --output-path - path to the SQL script to be generated%1$s"
      + "-p, --conn-props-path - path to the file containing properties for establishing "
      + "JDBC connection%1$s"
      + "Only one of the target operations may be accomplished at once - script generation or "
      + "direct database feeding through JDBC,%1$s"
      + "so only one of options -o, --output-path, -p or "
      + "--conn-props-path can be passed at the same moment.", System.lineSeparator());

  public static void main(String[] args) {
    if (args == null || args.length == 0) {
      System.out.println(HELP);
      return;
    }
  }
}
