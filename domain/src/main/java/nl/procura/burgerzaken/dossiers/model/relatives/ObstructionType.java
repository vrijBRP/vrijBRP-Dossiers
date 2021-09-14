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

public enum ObstructionType {
  EXISTING_RELOCATION_CASE,
  EXISTING_CONFIDENTIALITY_CASE,
  DIFFERENT_ADDRESS,
  MULTIPLE_PERSON_RECORDS_FOUND,
  NO_PERSON_RECORD_FOUND,
  RELATIONSHIP_HAS_ENDED,
  PERSON_IS_DECEASED,
  PERSON_IS_EMIGRATED,
  PERSON_RECORD_IS_BLOCKED,
  PERSON_RECORD_IS_SUSPENDED,
  PERSON_HAS_CURATOR,
  PERSON_HAS_CONFIDENTIALITY
}
