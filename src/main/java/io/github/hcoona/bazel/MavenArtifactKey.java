package io.github.hcoona.bazel;

import java.util.Objects;

public class MavenArtifactKey {
  private final String groupId;
  private final String artifactId;

  public MavenArtifactKey(String groupId, String artifactId) {
    this.groupId = groupId;
    this.artifactId = artifactId;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MavenArtifactKey that = (MavenArtifactKey) o;
    return groupId.equals(that.groupId) &&
      artifactId.equals(that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId);
  }
}
