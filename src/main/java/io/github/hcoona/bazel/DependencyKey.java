package io.github.hcoona.bazel;

import java.util.Objects;
import java.util.StringJoiner;

public class DependencyKey {
  private String groupId;
  private String artifactId;
  private String type = "jar";
  private String scope = "compile";
  private boolean optional = false;

  public String getGroupId() {
    return groupId;
  }

  public DependencyKey setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public DependencyKey setArtifactId(String artifactId) {
    this.artifactId = artifactId;
    return this;
  }

  public String getType() {
    return type;
  }

  public DependencyKey setType(String type) {
    if (type != null) {
      this.type = type;
    }
    return this;
  }

  public String getScope() {
    return scope;
  }

  public DependencyKey setScope(String scope) {
    if (scope != null) {
      this.scope = scope;
    }
    return this;
  }

  public boolean getOptional() {
    return optional;
  }

  public DependencyKey setOptional(boolean optional) {
    this.optional = optional;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DependencyKey that = (DependencyKey) o;
    return Objects.equals(groupId, that.groupId) &&
        Objects.equals(artifactId, that.artifactId) &&
        Objects.equals(type, that.type) &&
        Objects.equals(scope, that.scope) &&
        Objects.equals(optional, that.optional);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, type, scope, optional);
  }

  @Override
  public String toString() {
    return new StringJoiner(":")
        .add(groupId)
        .add(artifactId)
        .add(scope)
        .toString();
  }
}
