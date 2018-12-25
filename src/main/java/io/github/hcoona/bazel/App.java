package io.github.hcoona.bazel;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public final class App {
  private static final Logger LOG = LoggerFactory.getLogger(App.class);
  private static final String PROJECT_ROOT = "/Users/shuaiz/GitEnlistments/Hadoop";

  private App() {
  }

  public static void main(String[] args) {
    Path projectRootPath = Paths.get(PROJECT_ROOT);

    Map<String, String> properties = visitProperties(projectRootPath).collect(Collectors.toMap(
        entry -> entry.getKey().toString(),
        entry -> entry.getValue().toString(),
        (a, b) -> b));

    Map<DependencyKey, Set<String>> result = visitDependencies(projectRootPath)
        .collect(Collectors.groupingBy(
            d -> new DependencyKey()
                .setGroupId(d.getGroupId())
                .setArtifactId(d.getArtifactId())
                .setType(d.getType())
                .setScope(d.getScope())
                .setOptional(Boolean.parseBoolean(d.getOptional())),
            Collectors.collectingAndThen(
                Collectors.mapping(
                    d -> {
                      String version = d.getVersion();
                      if (version == null || version.isEmpty()) {
                        return null;
                      } else {
                        if (version.startsWith("${") && version.endsWith("}")) {
                          String k = version.substring(2, version.length() - 1);
                          String v = properties.get(k);
                          if (v == null) {
                            LOG.warn("Cannot found property key: {}", k);
                            return null;
                          } else {
                            return v;
                          }
                        } else {
                          return version;
                        }
                      }
                    },
                    Collectors.toSet()),
                vs -> {
                  vs.remove(null);
                  return vs;
                })));
    for (Map.Entry<DependencyKey, Set<String>> entry : result.entrySet()) {
      if (entry.getValue().size() != 1 && entry.getKey().getScope().equals("compile")) {
        LOG.warn("{} {}", entry.getKey(), entry.getValue());
      } else {
        LOG.info("{} {}", entry.getKey(), entry.getValue());
      }
    }
  }

  private static String getMavenCentralPomUrl(String groupId, String artifactId, String version) {
    String path = new StringJoiner("/")
        .add(groupId.replaceAll("\\.", "/"))
        .add(artifactId.replaceAll("\\.", "/"))
        .add(version)
        .toString();
    return "http://central.maven.org/maven2/" + path;
  }

  private static Stream<Dependency> visitDependencies(Path path) {
    Path pomPath = path.resolve("pom.xml");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try (InputStream pomInputStream = Files.newInputStream(pomPath)) {
      Model model = reader.read(pomInputStream);
      return StreamUtils.concat(
          model.getDependencies().stream(),
          Optional.ofNullable(model.getDependencyManagement())
              .map(DependencyManagement::getDependencies)
              .orElseGet(Collections::emptyList)
              .stream(),
          model.getModules().stream()
              .flatMap(module -> visitDependencies(path.resolve(module)))
      );
    } catch (IOException e) {
      LOG.error("Failed to open POM file", e);
    } catch (XmlPullParserException e) {
      LOG.error("Failed to parse POM file", e);
    }
    return Stream.empty();
  }

  private static Stream<Map.Entry<Object, Object>> visitProperties(Path path) {
    Path pomPath = path.resolve("pom.xml");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try (InputStream pomInputStream = Files.newInputStream(pomPath)) {
      Model model = reader.read(pomInputStream);
      return StreamUtils.concat(
          model.getProperties().entrySet().stream(),
          model.getModules().stream()
              .flatMap(module -> visitProperties(path.resolve(module)))
      );
    } catch (IOException e) {
      LOG.error("Failed to open POM file", e);
    } catch (XmlPullParserException e) {
      LOG.error("Failed to parse POM file", e);
    }
    return Stream.empty();
  }
}
