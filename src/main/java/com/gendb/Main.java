package com.gendb;

import static com.gendb.util.ArgumentTypes.CONFIG_PATH;
import static com.gendb.util.ArgumentTypes.CONNECTION_PROPERTIES_PATH;
import static com.gendb.util.ArgumentTypes.HELP;
import static com.gendb.util.ArgumentTypes.OUTPUT_SCRIPT_PATH;
import static com.gendb.util.ArgumentTypes.fromArg;

import com.gendb.exception.GenerationException;
import com.gendb.util.ArgumentTypes;
import com.gendb.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.validation.Validation;
import javax.validation.Validator;

public class Main {

  private static final String USAGE = String.format("Usage:%1$s"
      + "gendb -h|--help%1$s"
      + "gendb -c|--config-path CONFIG_FILE_PATH "
      + "(-o|--output-path OUTPUT_FILE_PATH) | "
      + "(-p|--conn-props-path CONNECTION_PROPERTIES_FILE_PATH)%1$s%1$s", System.lineSeparator());

  private static final String HELP_TEXT = String.format("Gendb fills realtional databases with test data%1$s"
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

  private static boolean printHelpMessage(final String[] args) {
    return args == null || args.length == 0 || fromArg(args[0]) == HELP;
  }

  private static boolean correctParams(final String[] args) {
    final boolean configPathPresent = fromArg(args[0]) == CONFIG_PATH || fromArg(args[2]) == CONFIG_PATH;
    final List<ArgumentTypes> OUTPUT_ARGS = Arrays.asList(OUTPUT_SCRIPT_PATH, CONNECTION_PROPERTIES_PATH);
    final boolean outputArgPresent = OUTPUT_ARGS.contains(fromArg(args[0])) || OUTPUT_ARGS.contains(fromArg(args[2]));
    return args.length == 4 && (configPathPresent || outputArgPresent);
  }

  private static void order(final String[] args) {
    if (fromArg(args[0]) == CONFIG_PATH) {
      return;
    }

    Utils.swap(args, 0, 2);
    Utils.swap(args, 1, 3);
  }


  public static void main(String[] args) throws IOException, GenerationException {
    if (printHelpMessage(args)) {
      System.out.println(HELP_TEXT);
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
    if (fromArg(args[3]) == CONNECTION_PROPERTIES_PATH && !Files.exists(secondArgPath)) {
      System.out.println(String.format(CONN_PROPS_FILE_NOT_FOUND, secondArgPath.toString()));
      return;
    }

    final Validator v = Validation.buildDefaultValidatorFactory().getValidator();
    final Generator g = new Generator(v);
    final InputStream input = Files.newInputStream(configPath);
    if (fromArg(args[2]) == OUTPUT_SCRIPT_PATH) {
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
