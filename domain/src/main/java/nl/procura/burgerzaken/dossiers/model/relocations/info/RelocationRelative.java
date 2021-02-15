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

package nl.procura.burgerzaken.dossiers.model.relocations.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

@Getter
public class RelocationRelative {

  private final String                          bsn;
  private final RelationshipType                relationshipType;
  private Integer                               age;
  private final List<RelocationObstructionType> obstructions = new ArrayList<>();

  public RelocationRelative(String bsn, RelationshipType relationshipType) {
    this.bsn = bsn;
    this.relationshipType = relationshipType;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Optional<DeclarationType> getDeclarationType() {
    switch (relationshipType) {
      case REGISTERED:
        return Optional.of(DeclarationType.REGISTERED);
      case PARTNER:
        return Optional.of(DeclarationType.PARTNER);
      case PARENT:
        return Optional.of(DeclarationType.ADULT_CHILD_LIVING_WITH_PARENTS);
      case CHILD:
        if (age == null || age < 18) {
          return Optional.of(DeclarationType.AUTHORITY_HOLDER);
        } else {
          return Optional.of(DeclarationType.PARENT_LIVING_WITH_ADULT_CHILD);
        }
      case EX_PARTNER:
        return Optional.empty();
    }
    throw new IllegalArgumentException("Unknown relationship type: " + relationshipType);
  }

  public void addObstruction(RelocationObstructionType obstruction) {
    obstructions.add(obstruction);
  }
}
