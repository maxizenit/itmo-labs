package org.cloudfoundry.samples.music.selenium.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Album {

  @NonNull private String title;

  @NonNull private String artist;

  @NonNull private Integer releaseYear;

  @NonNull private String genre;
}
