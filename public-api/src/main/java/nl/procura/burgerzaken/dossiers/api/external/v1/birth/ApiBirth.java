/*
 * Copyright 2022 - 2023 Procura B.V.
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

package nl.procura.burgerzaken.dossiers.api.external.v1.birth;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static nl.procura.burgerzaken.dossiers.model.dossier.DossierType.BIRTH;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.procura.burgerzaken.dossiers.api.external.v1.dossier.ApiDossier;
import nl.procura.burgerzaken.dossiers.model.birth.Birth;
import nl.procura.burgerzaken.dossiers.model.client.Client;
import nl.procura.burgerzaken.dossiers.model.dossier.Dossier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Birth")
public class ApiBirth {

  @Valid
  @Schema(name = "dossier", required = true)
  @NotNull(message = "dossier is mandatory")
  private ApiDossier dossier;

  @Schema(name = "qualificationForDeclaringType")
  private ApiQualificationForDeclaringType qualificationForDeclaringType;

  @Valid
  @Schema(name = "declarant", required = true)
  @NotNull(message = "declarant is mandatory")
  private ApiDeclarant declarant;

  @Valid
  @Schema(name = "mother", required = true)
  @NotNull(message = "mother is mandatory")
  private ApiMother mother;

  @Valid
  @Schema(name = "fatherDuoMother")
  ApiFatherDuoMother fatherDuoMother;

  @Valid
  @Schema(required = true)
  @NotNull(message = "children are mandatory")
  @Size(min = 1, message = "list of children may not be empty")
  private List<ApiChild> children;

  @Valid
  @Schema(name = "nameSelection", required = true)
  @NotNull(message = "nameSelection is mandatory")
  private ApiNameSelection nameSelection;

  public static ApiBirth of(Birth birth) {
    return ApiBirth.builder()
        .dossier(ApiDossier.of(birth.getDossier()))
        .qualificationForDeclaringType(Optional.ofNullable(birth.getQualificationForDeclaringType())
            .map(ApiQualificationForDeclaringType::valueOfType)
            .orElse(null))
        .declarant(birth.getDeclarant()
            .map(ApiDeclarant::of)
            .orElseThrow(() -> new IllegalArgumentException("No declarant")))
        .mother(birth.getMother()
            .map(ApiMother::of)
            .orElseThrow(() -> new IllegalArgumentException("No mother")))
        .fatherDuoMother(birth.getFatherDuoMother()
            .map(ApiFatherDuoMother::of)
            .orElse(null))
        .children(birth.getChildren().stream()
            .map(ApiChild::of)
            .collect(toList()))
        .nameSelection(ApiNameSelection.of(birth.getNameSelection()))
        .build();
  }

  public Birth createNew(Client client) {
    Dossier newDossier = this.dossier.createNew(BIRTH, client);
    Birth birth = new Birth(newDossier);
    ofNullable(qualificationForDeclaringType).ifPresent(type -> birth.setQualificationForDeclaringType(type.getType()));
    birth.setDeclarant(declarant.toPerson());
    birth.setMother(mother.toPerson());
    ofNullable(fatherDuoMother).ifPresent(fatherDuoMother -> birth.setFatherDuoMother(fatherDuoMother.toPerson()));
    birth.setNameSelection(nameSelection.toNameSelection());
    children.forEach(r -> birth.addChild(r.toChild()));
    return birth;
  }
}
