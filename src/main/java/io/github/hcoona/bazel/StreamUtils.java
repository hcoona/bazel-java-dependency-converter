package io.github.hcoona.bazel;

import java.util.Arrays;
import java.util.stream.Stream;

final class StreamUtils {
  private StreamUtils() {
  }

  @SafeVarargs
  public static <T> Stream<T> concat(Stream<T>... streams) {
    if (streams.length == 0) {
      return Stream.empty();
    } else if (streams.length == 1) {
      return streams[0];
    } else if (streams.length == 2) {
      return Stream.concat(streams[0], streams[1]);
    } else {
      return Arrays.stream(streams).reduce(Stream.empty(), Stream::concat);
    }
  }
}
