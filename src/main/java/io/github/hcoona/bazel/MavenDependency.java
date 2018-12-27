package io.github.hcoona.bazel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MavenDependency {
  private String groupId;
  private String artifactId;
  private String version;
  private boolean isOptional;
  private boolean isProject;
  private Set<MavenDependency> parents = new HashSet<>();
  private Set<MavenDependency> children = new HashSet<>();

  public static MavenDependency createProject(
    String groupId, String artifactId) {
    return createProject(groupId, artifactId);
  }

  public static MavenDependency createProject(
    String groupId, String artifactId, MavenDependency parent) {
    MavenDependency dependency = new MavenDependency()
      .setGroupId(groupId)
      .setArtifactId(artifactId)
      .setProject(true);

    if (parent != null) {
      dependency.parents.add(parent);
      parent.children.add(dependency);
    }

    // TODO: scan pom.xml for dependencies

    return dependency;
  }

  public static MavenDependency createDependency(
    String groupId, String artifactId, String version, boolean optional,
    MavenDependency parent, Set<MavenArtifactKey> exclusions) {
    MavenDependency dependency = new MavenDependency()
      .setGroupId(groupId)
      .setArtifactId(artifactId)
      .setVersion(version)
      .setOptional(optional)
      .setProject(false);

    dependency.parents.add(parent);
    parent.children.add(dependency);

    // TODO: find transition dependencies recursively, notice exclusions

    return dependency;
  }

  public String getGroupId() {
    return groupId;
  }

  public MavenDependency setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public MavenDependency setArtifactId(String artifactId) {
    this.artifactId = artifactId;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public MavenDependency setVersion(String version) {
    this.version = version;
    return this;
  }

  public boolean isOptional() {
    return isOptional;
  }

  public MavenDependency setOptional(boolean optional) {
    isOptional = optional;
    return this;
  }

  public boolean isProject() {
    return isProject;
  }

  public MavenDependency setProject(boolean project) {
    isProject = project;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MavenDependency that = (MavenDependency) o;
    return isOptional == that.isOptional &&
      isProject == that.isProject &&
      Objects.equals(groupId, that.groupId) &&
      Objects.equals(artifactId, that.artifactId) &&
      Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version, isOptional, isProject);
  }
}
