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

package nl.procura.burgerzaken.dossiers.model.relatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.procura.burgerzaken.gba.numbers.Bsn;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Relative {

  @Setter
  private DeclarationType             declarationType;
  @Setter
  private Integer                     age;
  private final List<SuitableForType> suitableFor  = new ArrayList<>();
  private final List<ObstructionType> obstructions = new ArrayList<>();

  private final Bsn              bsn;
  private final RelationshipType relationshipType;

  public Relative(Bsn bsn, RelationshipType relationshipType) {
    this.bsn = bsn;
    this.relationshipType = relationshipType;
  }

  public void addObstruction(ObstructionType type) {
    obstructions.add(type);
  }

  public void addSuitableFor(SuitableForType type) {
    suitableFor.add(type);
  }

  public boolean hasObstructions(ObstructionType... types) {
    return Arrays.stream(types)
        .anyMatch(type -> obstructions.stream()
            .anyMatch(obstructionType -> type == obstructionType));
  }
}
