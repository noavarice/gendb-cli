package com.gendb;

import com.gendb.exception.GenerationException;
import com.gendb.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.validation.Validation;
import javax.validation.Validator;

public class Main {

  private static final String USAGE = String.format("Usage:%1$s"
      + "gendb -h|--help%1$s"
      + "gendb -c|--config-path CONFIG_FILE_PATH "
      + "(-o|--output-path OUTPUT_FILE_PATH) | "
      + "(-p|--conn-props-path CONNECTION_PROPERTIES_FILE_PATH)%1$s%1$s", System.lineSeparator());

  private static final String HELP = String.format("Gendb fills realtional databases with test data%1$s"
      + USAGE
      + "Details:%1$s"
      + "-h, --help - prints this help message and exits"
      + "-c, --config-path - path to the configuration file%1$s"
      + "-o, --output-path - path to the SQL script to be generated%1$s"
      + "-p, --conn-props-path - path to the file containing properties for establishing "
      + "JDBC connection%1$s"
      + "Only one of the target operations may be accomplished at once - script generation or "
      + "direct database feeding through JDBC,%1$s"
      + "so only one of options -o, --output-path, -p or "
      + "--conn-props-path can be passed at the same moment.", System.lineSeparator());

  private static final String ILLEGAL_ARGUMENTS = "Illegal arguments" + System.lineSeparator()
      + USAGE;

  private static final String CONFIG_NOT_FOUND = "Configuration file '%1$s' not found";

  private static final String CONN_PROPS_FILE_NOT_FOUND = "JDBC connection properties file '%1$s' not found";

  private static boolean isHelpArg(final String arg) {
    return arg.equals("-h") || arg.equals("--help");
  }

  private static boolean isConfigPathArg(final String arg) {
    return arg.equals("-c") || arg.equals("--config-path");
  }

  private static boolean isOutputScriptPathArg(final String arg) {
    return arg.equals("-o") || arg.equals("--output-path");
  }

  private static boolean isConnPropertiesPathArg(final String arg) {
    return arg.equals("-p") || arg.equals("--conn-props-path");
  }

  private static boolean printHelpMessage(final String[] args) {
    return args == null || args.length == 0 || isHelpArg(args[0]);
  }

  private static boolean correctParams(final String[] args) {
    return args.length == 4 && (isConfigPathArg(args[0])
                            && (isOutputScriptPathArg(args[2]) || isConnPropertiesPathArg(args[2]))
                            || (isConfigPathArg(args[2])
                            && (isOutputScriptPathArg(args[0]) || isConnPropertiesPathArg(args[0]))));
  }

  private static void order(final String[] args) {
    if (isConfigPathArg(args[0])) {
      return;
    }

    Utils.swap(args, 0, 2);
    Utils.swap(args, 1, 3);
  }


  public static void main(String[] args) throws IOException, GenerationException {
    if (printHelpMessage(args)) {
      System.out.println(HELP);
      return;
    }

    if (!correctParams(args)) {
      System.out.println(ILLEGAL_ARGUMENTS);
      return;
    }

    order(args);
    final Path configPath = Paths.get(args[1]);
    if (!Files.exists(configPath)) {
      System.out.println(String.format(CONFIG_NOT_FOUND, configPath.toString()));
      return;
    }

    final Path secondArgPath = Paths.get(args[3]);
    if (isConnPropertiesPathArg(args[3]) && !Files.exists(secondArgPath)) {
      System.out.println(String.format(CONN_PROPS_FILE_NOT_FOUND, secondArgPath.toString()));
      return;
    }

    final Validator v = Validation.buildDefaultValidatorFactory().getValidator();
    final Generator g = new Generator(v);
    final InputStream input = Files.newInputStream(configPath);
    if (isOutputScriptPathArg(args[2])) {
      final OutputStream output = Files.newOutputStream(secondArgPath);
      g.createScript(input, output);
    } else {
      final InputStream propsStream = Files.newInputStream(secondArgPath);
      final Properties props = new Properties();
      props.load(propsStream);
      g.createDatabase(input, props);
    }
  }
}
