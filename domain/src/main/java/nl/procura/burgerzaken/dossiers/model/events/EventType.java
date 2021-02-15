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

package nl.procura.burgerzaken.dossiers.model.events;

/**
 * Event types are stored as string so don't change names.
 * <p>
 * Naming convention: [object type]_[past tense action]
 */
public enum EventType {
  DOSSIER_UPDATED,
  DOSSIER_DELETED,
  INTER_MUNICIPAL_RELOCATION_CREATED,
  INTRA_MUNICIPAL_RELOCATION_CREATED,
  INTRA_MUNICIPAL_RELOCATION_UPDATED,
  INTER_MUNICIPAL_RELOCATION_UPDATED,
  BIRTH_CREATED,
  COMMITMENT_CREATED,
  COMMITMENT_UPDATED,
  DEATH_CREATED
}
