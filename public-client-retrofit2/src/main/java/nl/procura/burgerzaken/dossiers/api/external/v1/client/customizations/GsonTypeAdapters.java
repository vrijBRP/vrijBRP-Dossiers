/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.burgerzaken.dossiers.api.external.v1.client.customizations;

import static java.time.format.DateTimeFormatter.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class GsonTypeAdapters {

  public static GsonBuilder registerAdapters(GsonBuilder builder) {
    builder.registerTypeAdapter(LocalDate.class, new CustomAdapter<>(
        ISO_DATE::format, from -> LocalDate.parse(from, ISO_DATE)));

    builder.registerTypeAdapter(LocalTime.class, new CustomAdapter<>(
        ISO_TIME::format, from -> LocalTime.parse(from, ISO_TIME)));

    builder.registerTypeAdapter(LocalDateTime.class, new CustomAdapter<>(
        ISO_DATE_TIME::format, from -> LocalDateTime.parse(from, ISO_DATE_TIME)));
    return builder;
  }

  public static class CustomAdapter<T> extends TypeAdapter<T> {

    private Function<T, String> writeConverter;
    private Function<String, T> readConverter;

    public CustomAdapter(Function<T, String> writeConverter, Function<String, T> readConverter) {
      this.writeConverter = writeConverter;
      this.readConverter = readConverter;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
      if (value == null) {
        out.nullValue();
      } else {
        out.value(writeConverter.apply(value));
      }
    }

    @Override
    public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }
      String obj = in.nextString();
      return readConverter.apply(obj);
    }
  }
}
