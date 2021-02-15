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

package nl.procura.burgerzaken.dossiers.oauth;

import javax.persistence.EntityManager;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OauthClientDetailsCustomRepositoryImpl implements OauthClientDetailsCustomRepository {

  private final EntityManager       entityManager;
  private final TransactionTemplate transactionTemplate;

  public OauthClientDetailsCustomRepositoryImpl(EntityManager entityManager,
      PlatformTransactionManager transactionManager) {
    this.entityManager = entityManager;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public boolean create(Client client, String encodedSecret) {
    OauthClientDetails details = new OauthClientDetails(client, encodedSecret);
    try {
      transactionalInsert(details);
    } catch (DataIntegrityViolationException e) {
      log.debug(e.getMessage(), e);
      return false;
    }
    return true;
  }

  public void transactionalInsert(OauthClientDetails details) {
    // cannot use @Transactional on this function so doing it this way:
    transactionTemplate.execute(status -> {
      entityManager.persist(details);
      return null;
    });
  }
}
