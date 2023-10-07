package transact.commons.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import transact.commons.core.Config;
import transact.commons.exceptions.DataLoadingException;

/**
 * A class for accessing the Config File.
 */
public class ConfigUtil {

  public static Optional<Config> readConfig(Path configFilePath) throws DataLoadingException {
    return JsonUtil.readJsonFile(configFilePath, Config.class);
  }

  public static void saveConfig(Config config, Path configFilePath) throws IOException {
    JsonUtil.saveJsonFile(config, configFilePath);
  }

}
